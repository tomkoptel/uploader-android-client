package com.olderworld.app.uploader

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.olderworld.feature.uploader.Uploader
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

internal class UploaderLifecycleObserver @Inject constructor(
    val uploader: Uploader
) : LifecycleObserver {
    private var disposable = Disposable.disposed()

    private val internalState = MutableLiveData<Uploader.State>()
    val state: LiveData<Uploader.State> = internalState

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        disposable = uploader.state().subscribeBy(
            onNext = { status ->
                internalState.postValue(status)
            },
            onError = {
                Timber.e(it, "UploaderLifecycleObserver encountered fatal")
            }
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        disposable.dispose()
    }
}
