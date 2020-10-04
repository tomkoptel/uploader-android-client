package com.olderworld.feature.uploader

import androidx.collection.arrayMapOf

internal class InMemoryTaskStore : TaskStore {
    private val store = arrayMapOf<String, Task>()

    override fun getAll(): Set<Task> {
        return store.values.toSet()
    }

    override fun getAllBy(state: TaskState): Set<Task> {
        return store.values.filter { it.status.type == state }.toSet()
    }

    override fun getBy(id: String): Task? = store[id]

    override fun save(task: Task) {
        synchronized(store) {
            store[task.id] = task
        }
    }
}
