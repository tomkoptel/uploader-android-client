package com.olderworld.app.uploader

import android.app.Activity
import android.content.Intent
import android.content.collectUris
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.content_main.view.*
import timber.log.Timber

@AndroidEntryPoint
internal class FilesFragment : Fragment(R.layout.content_main) {
    companion object {
        private const val PICK_FILE = 0xf11e
    }

    private val viewModel: FilesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter<String>(view.context, android.R.layout.simple_list_item_1)
        view.listView.adapter = adapter

        viewModel.bind()
        viewModel.state.observe(viewLifecycleOwner) { state ->
            updateUIState(state, adapter)
        }

        view.pickFiles?.setOnClickListener {
            selectFilesForUpload()
        }
        view.toolbar.setTitle(R.string.files_for_upload)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("onActivityResult(requestCode=$requestCode, resultCode=$resultCode, data=$data")
        if (requestCode == PICK_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val uris = data.collectUris()
                if (uris.isNotEmpty()) {
                    Intent(
                        context,
                        FileUploaderService::class.java
                    ).let {
                        it.action = BuildConfig.ACTION_UPLOAD
                        it.data = data.data
                        it.clipData = data.clipData
                        requireActivity().startService(it)
                    }
                }
            }
        }
    }

    private fun selectFilesForUpload() {
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

    private fun updateUIState(
        state: FilesViewModel.State?,
        adapter: ArrayAdapter<String>
    ) {
        when (state) {
            is FilesViewModel.State.NoUploads -> {
                adapter.add("No active uploads")
            }
            is FilesViewModel.State.ActiveUploads -> {
                adapter.clear()
                adapter.addAll(state.tasks)
            }
        }
        adapter.notifyDataSetChanged()
    }
}
