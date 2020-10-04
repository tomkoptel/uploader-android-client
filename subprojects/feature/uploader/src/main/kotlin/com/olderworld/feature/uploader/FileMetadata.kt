package com.olderworld.feature.uploader

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

fun Task.toFileMetadata(context: Context): FileMetadata? {
    return when (val metadata = this.metadata) {
        is AndroidTaskMetadata -> {
            context.contentResolver.openInputStream(metadata.uri)?.let { inputStream ->
                DocumentFile.fromSingleUri(context, metadata.uri)?.let {
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
            metadata.file.toFileMetadata()
        }
        is PathTaskMetadata -> {
            File(metadata.path).toFileMetadata()
        }
        else -> throw IllegalArgumentException("We can not process metadata of type $this")
    }
}

private fun File.toFileMetadata() = FileMetadata(
    length = length(),
    name = name
) { FileInputStream(this) }

class FileMetadata(
    val length: Long,
    val name: String,
    val mimeType: String,
    internal val inputStreamProvider: () -> InputStream
) {
    val inputStream: InputStream get() = inputStreamProvider()

    internal companion object {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileMetadata

        if (length != other.length) return false
        if (name != other.name) return false
        if (mimeType != other.mimeType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = length.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + mimeType.hashCode()
        return result
    }

    override fun toString(): String {
        return "FileMetadata(length=$length, name='$name', mimeType='$mimeType')"
    }
}
