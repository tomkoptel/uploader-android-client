package com.olderworld.app.uploader

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.format.Formatter
import android.view.View
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.jakewharton.byteunits.DecimalByteUnit
import kotlinx.android.synthetic.main.content_main.view.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class FilesFragment : Fragment(R.layout.content_main) {
    companion object {
        private const val PICK_FILE = 0xf11e
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.pickFiles?.setOnClickListener {
            val packageManager = requireActivity().packageManager
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                type = "*/*"

                // TODO consider poiniting to the more friendly location e.g. downloads
                // Optionally, specify a URI for the directory that should be opened in
                // the system file picker before your app creates the document.
                // putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
            }
            val component = intent.resolveActivity(packageManager)
            if (component == null) {
                Timber.d("Can not resolve intent=$intent")
                // so sad
            } else {
                // TODO convert to the modern way to handle events
                startActivityForResult(intent, PICK_FILE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("onActivityResult(requestCode=$requestCode, resultCode=$resultCode, data=$data")
        if (requestCode == PICK_FILE && resultCode == Activity.RESULT_OK) {
            val uris = data?.collectUris() ?: return
            uris.forEach { dumpUri(it) }
        }
    }

    private fun dumpUri(fileUri: Uri) {
        // argh, Android why?
        val context = requireActivity()
        val contentResolver = context.contentResolver
        contentResolver.takePersistableUriPermission(
            fileUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        Intent(
            BuildConfig.ACTION_UPLOAD,
            fileUri,
            context,
            FileUploaderService::class.java
        ).let { context.startService(it) }

        DocumentFile.fromSingleUri(context, fileUri)?.let { documentFile ->
            documentFile.timberDebugInfo()

            when {
                documentFile.isFile -> {
                    documentFile.readCursor(contentResolver) {
                        val sizeIndex = getColumnIndex(OpenableColumns.SIZE)
                        moveToFirst()
                        val size = getLong(sizeIndex)
                        if (isNull(sizeIndex)) {
                            Timber.d("So the size is not accessible :(")
                        } else {
                            val humanSize = Formatter.formatFileSize(context, size)
                            val sizeInMB =
                                DecimalByteUnit.MEGABYTES.convert(size, DecimalByteUnit.BYTES)
                            val isFileUnder10mb = sizeInMB <= 10
                            Timber.d("So the size is $humanSize isFileUnder10mb=$isFileUnder10mb")
                        }
                    }

                    // TODO it is probably better idea to safe file first to the cache and then upload it ?
                    // https://developer.android.com/training/data-storage/app-specific#query-free-space
                    viewLifecycleOwner.lifecycleScope.launch {
                        try {
                            val text = documentFile.readAsText(contentResolver)
                            val printTrimValue = 10.coerceAtMost(text.length)
                            val textTrim = text.substring(0, printTrimValue)
                            Timber.d("First 10 chars of file \n $textTrim")
                        } catch (ex: IOException) {
                            Timber.e("Can not read file content as string")
                        }
                    }
                }
                documentFile.isVirtual -> {
                    // TODO handle as virtual https://developer.android.com/training/data-storage/shared/documents-files#open-virtual-file
                }
                else -> {
                    Timber.d("document=${documentFile.uri} not a file ignore")
                }
            }
        }
    }

    private fun DocumentFile.readCursor(
        contentResolver: ContentResolver,
        accessor: Cursor.() -> Unit
    ) {
        contentResolver.query(uri, null, null, null, null)?.use(accessor)
    }

    private fun DocumentFile.readAsText(contentResolver: ContentResolver): String {
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }

    private fun Intent.collectUris(): List<Uri> {
        return when (val clipData = this.clipData) {
            null -> listOfNotNull(data)
            else -> {
                val list = mutableListOf<Uri>()
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    list.add(uri)
                }
                return list
            }
        }
    }

    private fun DocumentFile.timberDebugInfo() {
        val directory = this.isDirectory
        val isFile = this.isFile
        val isVirtual = this.isVirtual
        val name = this.name
        val canRead = this.canRead()
        val uri = this.uri
        Timber.d(
            """
                    directory=$directory,
                    isFile=$isFile,
                    isVirtual=$isVirtual
                    name=$name
                    canRead=$canRead
                    uri=$uri
                """.trimIndent()
        )
    }
}
