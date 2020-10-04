package com.olderworld.feature.uploader

import io.reactivex.rxjava3.core.Single
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.File
import java.io.FileNotFoundException

@Throws(FileNotFoundException::class)
internal fun progressRequestBody(
    file: File,
    mimeType: String = "application/octet-stream",
    onProgress: (progress: Int) -> Unit
): RequestBody {
    val mediaType = mimeType.toMediaType()
    return ProgressRequestBody(mediaType, file, onProgress)
}

/**
 * Transforms a [RequestBody] into one that is reactive and will emit progress updates
 * to the provide `Emitter`.
 */
private class ProgressRequestBody(
    private val mediaType: MediaType,
    private val file: File,
    private val progressEmitter: (progress: Int) -> Unit
) : RequestBody() {
    override fun contentType() = mediaType

    override fun contentLength() = file.length()

    override fun writeTo(sink: BufferedSink) {
        file.source().use { source ->
            val total = contentLength()
            var remaining = total
            var size = BUFFER_SIZE.coerceAtMost(remaining)
            progressEmitter(0)

            while (remaining > 0) {
                sink.write(source, size)
                remaining = 0L.coerceAtLeast(remaining - size)
                size = BUFFER_SIZE.coerceAtMost(remaining)

                val progress = (100 - remaining.toFloat() / total * 100).toInt()
                progressEmitter(progress)
            }
        }
    }

    companion object {
        private const val BUFFER_SIZE = 8 * 1024.toLong()
    }
}
