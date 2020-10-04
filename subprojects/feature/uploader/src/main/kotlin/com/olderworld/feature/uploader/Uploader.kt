package com.olderworld.feature.uploader

import io.reactivex.rxjava3.core.Flowable
import java.util.Collections

internal fun Uploader.enqueue(task: Task) = this.enqueue(Collections.singleton(task))

internal interface Uploader {
    fun enqueue(tasks: Collection<Task>)

    fun status(): Flowable<Task.Status>

    fun state(): Flowable<State>

    data class State(
        val pending: Int = 0,
        val sending: Int = 0,
        val terminated: Int = 0,
        val completed: Int = 0,
    )
}
