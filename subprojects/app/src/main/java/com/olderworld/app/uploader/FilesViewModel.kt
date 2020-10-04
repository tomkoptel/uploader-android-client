package com.olderworld.app.uploader

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.olderworld.feature.uploader.RxTasks
import com.olderworld.feature.uploader.Task
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
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
        rxTasks.updates.subscribeBy(
            onNext = {
                internalState.postValue(State.ActiveUploads(it))
            },
            onError = {
                Timber.e(it, "Failed to consume task updates")
            }
        ).addTo(compositeDisposable)
    }

    sealed class State {
        object NoUploads : State()
        data class ActiveUploads(val tasks: Set<Task>) : State() {
            val asStringList by lazy {
                tasks.map { "id=${it.id} status=${it.status.type} progress=${it.status.progress}" }
            }
        }
    }
}
