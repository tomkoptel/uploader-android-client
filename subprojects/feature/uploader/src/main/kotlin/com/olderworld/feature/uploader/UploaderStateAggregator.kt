package com.olderworld.feature.uploader

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler

internal class UploaderStateAggregator(
    private val taskStore: TaskStore,
    private val scheduler: Scheduler
) {
    fun aggregate(taskStatus: Flowable<Task.Status>): Flowable<Uploader.State> {
        return taskStatus.switchMap {
            Flowable.fromCallable {
                Uploader.State(
                    pending = count(Task.Status.Type.PENDING),
                    sending = count(Task.Status.Type.SENDING),
                    completed = count(Task.Status.Type.COMPLETED),
                    terminated = count(Task.Status.Type.TERMINATED)
                )
            }.subscribeOn(scheduler)
        }.distinctUntilChanged()
    }

    private fun count(state: TaskState) = taskStore.getAllBy(state).size
}
