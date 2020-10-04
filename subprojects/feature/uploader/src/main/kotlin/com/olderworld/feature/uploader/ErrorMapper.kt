package com.olderworld.feature.uploader

import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException

internal fun Throwable.toErrorCause(): Task.ErrorCause = when (this) {
    is IOException -> Task.ErrorCause.IO_ERROR
    is FileNotFoundException -> Task.ErrorCause.FILE_NOT_FOUND
    else -> {
        Timber.e(this, "Unrecognized error happened during task upload")
        Task.ErrorCause.UNKNOWN
    }
}
