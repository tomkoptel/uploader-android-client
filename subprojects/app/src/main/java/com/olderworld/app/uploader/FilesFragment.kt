package com.olderworld.app.uploader

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.content_main.view.*

@AndroidEntryPoint
internal class FilesFragment : Fragment(R.layout.content_main) {
    private val viewModel: FilesViewModel by viewModels()

    lateinit var filePicker: FilePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filePicker = filePicker()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter<String>(view.context, android.R.layout.simple_list_item_1)
        view.listView.adapter = adapter

        viewModel.bind()
        viewModel.state.observe(viewLifecycleOwner) { state ->
            updateUIState(state, adapter)
        }

        view.pickFiles?.setOnClickListener {
            filePicker.pickMultiple {
                activity?.let { activity ->
                    activity.startService(FileUploaderService.intent(activity, it))
                }
            }
        }
        view.toolbar?.setTitle(R.string.files_for_upload)
    }

    private fun updateUIState(
        state: FilesViewModel.State?,
        adapter: ArrayAdapter<String>
    ) {
        adapter.clear()
        when (state) {
            is FilesViewModel.State.NoUploads -> {
                adapter.add("No active uploads")
            }
            is FilesViewModel.State.ActiveUploads -> {
                adapter.addAll(state.tasks)
            }
        }
        adapter.notifyDataSetChanged()
    }
}
