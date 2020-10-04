package com.olderworld.app.uploader

import android.app.Application
import android.text.format.Formatter
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.olderworld.feature.uploader.RxTasks
import com.olderworld.feature.uploader.Task
import com.olderworld.feature.uploader.toFileMetadata
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

internal class FilesViewModel @ViewModelInject constructor(
    application: Application,
    private val rxTasks: RxTasks
) : AndroidViewModel(application) {
    private val compositeDisposable = CompositeDisposable()

    private val internalState = MutableLiveData<State>()
    val state: LiveData<State> = internalState

    fun bind() {
        internalState.value = State.NoUploads
        rxTasks.updates
            .subscribeOn(Schedulers.computation())
            .map { tasks ->
                tasks.mapNotNull { task ->
                    task.toFileMetadata(getApplication())?.let { fileMetadata ->
                        val status = task.status
                        val progress = status.progress
                        val name = fileMetadata.name
                        val humanSize = Formatter.formatFileSize(getApplication(), fileMetadata.length)

                        when (val state = status.type) {
                            Task.Status.Type.PENDING, Task.Status.Type.COMPLETED -> {
                                "$name\n$humanSize\n$state"
                            }
                            Task.Status.Type.SENDING -> {
                                "$name\n$humanSize\n$state=$progress%"
                            }
                            Task.Status.Type.TERMINATED -> {
                                "$name\n$humanSize\n$state=${status.errorCause}"
                            }
                        }
                    }
                }
            }
            .subscribeBy(
                onNext = {
                    internalState.postValue(State.ActiveUploads(it))
                },
                onError = {
                    Timber.e(it, "Failed to consume task updates")
                }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

    sealed class State {
        object NoUploads : State()
        data class ActiveUploads(val tasks: List<String>) : State()
    }
}
