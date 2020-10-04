package com.olderworld.feature.uploader

internal sealed class UploadState {
    internal data class Uploading(val progress: Int) : UploadState()
    internal data class Failed(val responseCode: Int) : UploadState()
    internal object Success : UploadState()
}
