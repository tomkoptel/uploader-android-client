package com.olderworld.feature.uploader

internal data class UploadHttpError(val responseCode: Int) : Throwable("Failed to reach network")
