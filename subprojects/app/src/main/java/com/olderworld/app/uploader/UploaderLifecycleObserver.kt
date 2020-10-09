package com.olderworld.app.uploader

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.olderworld.feature.uploader.Uploader
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

internal fun Uploader.onStateChange(
    lifecycleOwner: LifecycleOwner,
    result: (state: Uploader.State) -> Unit
) {
    val observer = UploaderLifecycleObserver(this)
    lifecycleOwner.lifecycle.addObserver(observer)
    observer.state.observe(lifecycleOwner) { result(it) }
}

internal class UploaderLifecycleObserver @Inject constructor(
    private val uploader: Uploader
) : DefaultLifecycleObserver {
    private var disposable = Disposable.disposed()

    private val internalState = MutableLiveData<Uploader.State>()
    val state: LiveData<Uploader.State> = internalState

    override fun onCreate(owner: LifecycleOwner) {
        disposable = uploader.state().subscribeBy(
            onNext = { status ->
                internalState.postValue(status)
            },
            onError = {
                Timber.e(it, "UploaderLifecycleObserver encountered fatal")
            }
        )
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.dispose()
    }
}
