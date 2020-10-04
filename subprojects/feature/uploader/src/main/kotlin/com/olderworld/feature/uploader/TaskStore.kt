package com.olderworld.feature.uploader

internal interface TaskStore {
    fun getAll(): Set<Task>
    fun getAllBy(state: TaskState): Set<Task>
    fun getBy(id: String): Task?
    fun save(task: Task)
}
