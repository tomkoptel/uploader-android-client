package com.olderworld.feature.uploader

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

internal fun TaskStore.observer(): TaskStoreObserver {
    val delegate = this
    return TaskStoreObserver(delegate, Schedulers.computation())
}

internal class TaskStoreObserver(
    private val delegate: TaskStore,
    private val scheduler: Scheduler
) : TaskStore by delegate, RxTasks {
    private val processor = PublishProcessor.create<Long>()

    override fun save(task: Task) {
        delegate.save(task)
        processor.onNext(System.currentTimeMillis())
    }

    override val updates: Flowable<Set<Task>> = processor.onBackpressureLatest()
        .switchMap {
            Flowable.fromCallable { delegate.getAll() }.subscribeOn(scheduler)
        }
        .doOnError {
            Timber.e(it, "after transform")
        }
}
