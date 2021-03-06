package com.olderworld.feature.uploader

import android.net.Uri
import java.io.File
import java.util.UUID


fun Uri.asTask() = Task(AndroidTaskMetadata(this))

fun File.asTask() = Task(FileTaskMetadata(this))

fun String.asTask() = Task(PathTaskMetadata(this))

internal data class AndroidTaskMetadata(val uri: Uri) : Task.Metadata
internal data class FileTaskMetadata(val file: File) : Task.Metadata
internal data class PathTaskMetadata(val path: String) : Task.Metadata

internal typealias TaskState = Task.Status.Type

data class Task internal constructor(
    val id: String,
    val status: Status,
    internal val metadata: Metadata,
    internal val createdAt: Long,
) {
    internal companion object {
        operator fun invoke(metadata: Metadata): Task {
            val taskId = UUID.randomUUID().toString()
            return Task(
                id = taskId,
                metadata = metadata,
                createdAt = System.currentTimeMillis(),
                status = Status(taskId)
            )
        }
    }

    internal interface Metadata

    data class Status internal constructor(
        val taskId: String,
        val type: Type = Type.PENDING,
        val progress: Int = 0,
        val errorCause: ErrorCause? = null
    ) {
        val isPending: Boolean get() = type == Type.PENDING

        fun terminated(errorCause: ErrorCause) =
            copy(progress = 0, type = Type.TERMINATED, errorCause = errorCause)

        fun sending(progress: Int) =
            copy(progress = progress, type = Type.SENDING, errorCause = null)

        fun completed() = copy(progress = 100, type = Type.COMPLETED, errorCause = null)

        enum class Type {
            PENDING, SENDING, COMPLETED, TERMINATED;
        }
    }

    enum class ErrorCause {
        IO_ERROR,
        FILE_NOT_FOUND,
        HTTP_ERROR,
        UNKNOWN,
    }
}
