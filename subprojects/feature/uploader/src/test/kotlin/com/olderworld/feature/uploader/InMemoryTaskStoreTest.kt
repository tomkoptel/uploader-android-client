package com.olderworld.feature.uploader

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainSame
import org.junit.Test

internal class InMemoryTaskStoreTest {
    private val store = InMemoryTaskStore()

    @Test
    fun `save then retry`() {
        val task = "hello".asTask()
        store.save(task)
        store.getBy(task.id) shouldBeEqualTo task
    }

    @Test
    fun `should filter by status`() {
        val pending1Task = "pending1".asTask()
        val pending2Task = "pending2".asTask()
        store.save(pending1Task)
        store.save(pending2Task)

        val pendingTasks = store.getAllBy(state = Task.Status.Type.PENDING)
        pendingTasks shouldContainSame setOf(pending1Task, pending2Task)

        val sending1Task = "sending1".asTask().halfSent()
        val sending2Task = "sending2".asTask().halfSent()
        store.save(sending1Task)
        store.save(sending2Task)

        val sendingTasks = store.getAllBy(state = Task.Status.Type.SENDING)
        sendingTasks shouldContainSame setOf(sending1Task, sending2Task)
    }

    private fun Task.halfSent(): Task = run {
        copy(status = status.sending(progress = 50))
    }
}
