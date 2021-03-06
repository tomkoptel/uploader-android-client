package com.olderworld.feature.uploader

import io.reactivex.rxjava3.core.Flowable
import java.util.Collections

fun Uploader.enqueue(task: Task) = this.enqueue(Collections.singleton(task))

interface Uploader {
    fun enqueue(tasks: Collection<Task>)

    fun status(): Flowable<Task.Status>

    fun state(): Flowable<State>

    data class State internal constructor(
        val pending: Int = 0,
        val sending: Int = 0,
        val terminated: Int = 0,
        val completed: Int = 0,
    ) {
        val total get() = pending + sending + terminated + completed

        val progress: Int
            get() {
                if (total == 0) return 0
                return (((completed + terminated) * 100) / total)
            }

        val isFinished: Boolean get() = progress == 100
    }
}
