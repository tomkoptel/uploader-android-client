package com.olderworld.feature.uploader

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.FileNotFoundException

@Throws(FileNotFoundException::class)
internal fun FileMetadata.toProgressRequestBody(
    onProgress: (progress: Int) -> Unit
): RequestBody {
    val mediaType = mimeType.toMediaType()
    return ProgressRequestBody(mediaType, this, onProgress)
}

/**
 * Transforms a [RequestBody] into one that is reactive and will emit progress updates
 * to the provide `Emitter`.
 */
private class ProgressRequestBody(
    private val mediaType: MediaType,
    private val fileMetadata: FileMetadata,
    private val progressEmitter: (progress: Int) -> Unit
) : RequestBody() {
    override fun contentType() = mediaType

    override fun contentLength() = fileMetadata.length

    override fun writeTo(sink: BufferedSink) {
        fileMetadata.inputStream.source().use { source ->
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
