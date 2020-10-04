package com.olderworld.feature.uploader

import javax.inject.Inject

internal class UpdateTaskStatus @Inject constructor(
    private val taskStore: TaskStore
) {
    fun update(status: Task.Status): Task? {
        val task = taskStore.getBy(status.taskId)
        return task?.copy(status = status)
            ?.also { taskStore.save(it) }
    }
}
