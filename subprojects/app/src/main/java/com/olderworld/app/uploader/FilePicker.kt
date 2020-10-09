package com.olderworld.app.uploader

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal fun Fragment.filePicker() =
    FilePicker(requireActivity().activityResultRegistry)
        .also { this.lifecycle.addObserver(it) }

internal class FilePicker(
    private val registry: ActivityResultRegistry
) : DefaultLifecycleObserver {
    private var onResult: ((fileUris: List<Uri>) -> Unit)? = null
    lateinit var getContent: ActivityResultLauncher<String>

    fun pickMultiple(onResult: (fileUris: List<Uri>) -> Unit) {
        this.onResult = onResult
        getContent.launch("*/*")
    }

    override fun onCreate(owner: LifecycleOwner) {
        getContent = registry.register(
            FilePicker::class.java.simpleName,
            owner,
            GetMultipleContents()
        ) { onResult?.invoke(it) }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        onResult = null
    }
}
