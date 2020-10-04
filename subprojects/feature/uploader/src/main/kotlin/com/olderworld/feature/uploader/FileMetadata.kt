package com.olderworld.feature.uploader

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

internal fun Task.Metadata.toFileMetadata(context: Context): FileMetadata? = when (this) {
    is AndroidTaskMetadata -> {
        context.contentResolver.openInputStream(uri)?.let { inputStream ->
            DocumentFile.fromSingleUri(context, uri)?.let {
                FileMetadata(
                    length = it.length(),
                    name = it.name,
                    mimeType = it.type
                ) {
                    inputStream
                }
            }
        }
    }
    is FileTaskMetadata -> {
        FileMetadata(
            length = file.length(),
            name = file.name
        ) { FileInputStream(file) }
    }
    is PathTaskMetadata -> {
        val file = File(path)
        FileMetadata(
            length = file.length(),
            name = file.name,
        ) { FileInputStream(file) }
    }
    else -> throw IllegalArgumentException("We can not process metadata of type $this")
}

internal data class FileMetadata(
    val length: Long,
    val name: String,
    val mimeType: String,
    val inputStreamProvider: () -> InputStream
) {
    val inputStream: InputStream get() = inputStreamProvider()

    companion object {
        operator fun invoke(
            length: Long,
            name: String? = null,
            mimeType: String? = null,
            inputStreamProvider: () -> InputStream,
        ) = FileMetadata(
            length = length,
            name = name ?: "undefined-name",
            mimeType = mimeType ?: "application/octet-stream",
            inputStreamProvider = inputStreamProvider
        )
    }
}
