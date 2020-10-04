package com.olderworld.feature.uploader.data

import com.olderworld.feature.uploader.Task
import com.olderworld.feature.uploader.UploadState
import com.olderworld.feature.uploader.asFile
import com.olderworld.feature.uploader.progressRequestBody
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.Flowables
import io.reactivex.rxjava3.kotlin.subscribeBy
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.FileNotFoundException

internal fun Api.uploadActionFactory(): (Task) -> Flowable<Task.Status> {
    return { task ->
        task.upload(this)
            .map { uploadState ->
                when (uploadState) {
                    UploadState.Success -> {
                        task.status.completed()
                    }
                    is UploadState.Uploading -> {
                        task.status.sending(uploadState.progress)
                    }
                    is UploadState.Failed -> {
                        task.status.terminated(Task.ErrorCause.HTTP_ERROR)
                    }
                }
            }
    }
}

internal fun Task.upload(api: Api): Flowable<UploadState> = try {
    val file = metadata.asFile()

    Flowables.create<UploadState>(BackpressureStrategy.LATEST) { emitter ->
        try {
            val body = progressRequestBody(file) {
                emitter.onNext(UploadState.Uploading(it))
            }
            val metadata = file.name.asFileMetadata().toRequestBody(
                contentType = "application/json".toMediaType()
            )
            api.upload(body, metadata).subscribeBy(
                onSuccess = { response ->
                    if (response.isSuccessful) {
                        emitter.onNext(UploadState.Success)
                    } else {
                        emitter.onNext(UploadState.Failed(response.code()))
                    }
                    emitter.onComplete()
                },
                onError = {
                    emitter.onError(it)
                }
            )
        } catch (ex: FileNotFoundException) {
            emitter.onError(ex)
        }
    }
        /**
         * The only valid case for duplicates is when we rely on logging interceptor.
         * That invokes second round of byte read operation from the request body.
         * We don't want those fake reads to impact upstream, thus we are dropping duplicates.
         */
        .distinct()
} catch (ex: FileNotFoundException) {
    Flowable.error(ex)
}

