package com.olderworld.feature.uploader

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.processors.PublishProcessor
import timber.log.Timber

internal class UploaderImpl(
    private val updateTaskStatus: UpdateTaskStatus,
    private val persistScheduler: Scheduler,
    private val taskStore: TaskStore,
    private val uploaderStateAggregator: UploaderStateAggregator,
    private val uploadActionFactory: (Task) -> Flowable<Task.Status>
) : Uploader {
    private val taskSubject = PublishProcessor.create<Task>().toSerialized()
    private val statusSubject = PublishProcessor.create<Task.Status>().toSerialized()
    private val compositeDisposable = CompositeDisposable()

    private val statusObservable: Flowable<Task.Status>

    init {
        val createNewTasks = taskSubject
            .filter { it.status.isPending }
            .flatMapSingle { task ->
                Completable.fromCallable { taskStore.save(task) }
                    .toSingleDefault(task.status)
                    .onErrorReturn { task.status.terminated(it.toErrorCause()) }
                    .subscribeOn(persistScheduler)
            }

        val uploadTasks = taskSubject
            // Ensure that we do emit initial status
            .flatMap { task ->
                uploadActionFactory(task)
                    // We are only interested in running/error state
                    .filter { !it.isPending }
                    .doOnNext { Timber.d("upload task emits $it") }
                    .doOnError {
                        Timber.e(
                            it,
                            "upload task fatally ended ${taskStore.getBy(task.id)})"
                        )
                    }
                    .doOnComplete { Timber.d("upload task completed ${taskStore.getBy(task.id)}") }
                    .onErrorReturn { task.status.terminated(it.toErrorCause()) }
                    .subscribeOn(persistScheduler)
            }

        // TODO Check failure of persisting the value. What would happen?
        val updateTaskStatus = statusSubject
            .filter { !it.isPending }
            .flatMapMaybe { status ->
                Maybe.fromCallable<Task> { updateTaskStatus.update(status) }
                    .map { it.status }
                    .onErrorReturn { status.terminated(it.toErrorCause()) }
                    .subscribeOn(persistScheduler)
            }

        statusObservable = statusSubject
            .share()
            .onBackpressureLatest()

        createNewTasks.subscribeBy(
            onNext = statusSubject::onNext,
            onError = {
                Timber.e(it, "createNewTasks.onError(${it.message})")
            }
        ).addTo(compositeDisposable)

        uploadTasks.subscribeBy(
            onNext = statusSubject::onNext,
            onError = {
                Timber.e(it, "uploadTasks.onError(${it.message})")
            }
        ).addTo(compositeDisposable)

        updateTaskStatus.subscribeBy(
            onError = {
                Timber.e(it, "updateTaskStatus.onError(${it.message})")
            }
        ).addTo(compositeDisposable)
    }

    override fun enqueue(tasks: Collection<Task>) {
        tasks.forEach { taskSubject.onNext(it) }
    }

    override fun status(): Flowable<Task.Status> = statusObservable
        .doOnNext { Timber.d("status emits $it") }

    override fun state() = uploaderStateAggregator.aggregate(statusObservable)
}
