package com.olderworld.feature.uploader

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.Test
import timber.log.Timber
import java.io.IOException

internal class UploaderTest {
    private val updateTaskStatus: UpdateTaskStatus = mockk()
    private val uploadActionFactory: (Task) -> Flowable<Task.Status> = mockk()
    private val taskStore: TaskStore = mockk(relaxed = true)
    private val uploaderStateAggregator: UploaderStateAggregator = mockk()
    private val uploader: Uploader = UploaderImpl(
        updateTaskStatus = updateTaskStatus,
        persistScheduler = Schedulers.trampoline(),
        taskStore = taskStore,
        uploaderStateAggregator = uploaderStateAggregator,
        uploadActionFactory = uploadActionFactory
    )

    private val networkError = IOException("Network")

    init {
        Timber.plant(TimberTestTree())
    }

    @Test
    fun `querying the job should start it`() {
        val kittenTask = "kitten.jpg".asTask()
        val pending = kittenTask.status
        val completed = kittenTask.status.completed()

        updateTaskStatus reflectsStatusFor kittenTask
        every { uploadActionFactory(kittenTask) } returns Flowable.just(
            pending,
            completed
        )

        val states = uploader.status().test().values()

        uploader.enqueue(kittenTask)

        states.shouldNotBeEmpty()
        states shouldContain pending
        states shouldContain completed

        verify { taskStore.save(kittenTask) }
        verify { updateTaskStatus.update(completed) }
    }

    @Test
    fun `any failure during upload should be recognized as an error`() {
        val kittenTask = "kitten.jpg".asTask()
        val pending = kittenTask.status
        val ioError = kittenTask.status.terminated(Task.ErrorCause.IO_ERROR)

        updateTaskStatus reflectsStatusFor kittenTask
        every { uploadActionFactory(kittenTask) } returns Flowable.error(networkError)

        val states = uploader.status().test().values()

        uploader.enqueue(kittenTask)

        states.shouldNotBeEmpty()
        states shouldContain pending
        states shouldContain ioError

        verify { taskStore.save(kittenTask) }
        verify { updateTaskStatus.update(ioError) }
    }

    private infix fun UpdateTaskStatus.reflectsStatusFor(task: Task) {
        val self = this
        every { self.update(any()) } answers {
            val status = this.args.first() as Task.Status
            task.copy(status = status)
        }
    }
}
