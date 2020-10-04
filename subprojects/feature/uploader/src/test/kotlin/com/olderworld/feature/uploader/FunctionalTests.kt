package com.olderworld.feature.uploader

import com.olderworld.feature.uploader.data.api
import com.olderworld.feature.uploader.data.baseRetrofit
import com.olderworld.feature.uploader.data.upload
import com.olderworld.feature.uploader.data.uploadActionFactory
import com.olderworld.feature.uploader.data.verboseLogging
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okreplay.OkReplay
import okreplay.OkReplayConfig
import okreplay.OkReplayInterceptor
import okreplay.RecorderRule
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldContainSame
import org.junit.Rule
import org.junit.Test
import timber.log.Timber
import java.net.URL
import java.net.URLClassLoader
import java.util.concurrent.TimeUnit

internal class FunctionalTests {
    private val okReplayInterceptor = OkReplayInterceptor()
    private val api by lazy {
        OkHttpClient.Builder()
            .verboseLogging()
            .addInterceptor(okReplayInterceptor)
            .writeTimeout(1L, TimeUnit.MINUTES)
            .readTimeout(1L, TimeUnit.MINUTES)
            .build().let {
                baseRetrofit(it).build()
            }.api()
    }

    private val okReplayConfig = OkReplayConfig.Builder()
        .interceptor(okReplayInterceptor)
        .build()

    @get:Rule
    val okReplay = RecorderRule(okReplayConfig)

    init {
        Timber.plant(TimberTestTree())
    }

    @Test
    @OkReplay("upload_kitten")
    fun `happy case of 1 upload with task flowable`() {
        kitten { url ->
            val testObserver = url.file.asTask()
                .upload(api)
                .test()

            testObserver.await(10, TimeUnit.SECONDS)
            testObserver.values() shouldContainSame listOf(
                UploadState.Uploading(progress = 0),
                UploadState.Uploading(progress = 6),
                UploadState.Uploading(progress = 13),
                UploadState.Uploading(progress = 19),
                UploadState.Uploading(progress = 26),
                UploadState.Uploading(progress = 33),
                UploadState.Uploading(progress = 39),
                UploadState.Uploading(progress = 46),
                UploadState.Uploading(progress = 52),
                UploadState.Uploading(progress = 59),
                UploadState.Uploading(progress = 66),
                UploadState.Uploading(progress = 72),
                UploadState.Uploading(progress = 79),
                UploadState.Uploading(progress = 85),
                UploadState.Uploading(progress = 92),
                UploadState.Uploading(progress = 99),
                UploadState.Uploading(progress = 100),
                UploadState.Success
            )
        }
    }

    @Test
    @OkReplay("upload_kitten")
    fun `happy case of 1 upload with uploader`() {
        val taskStore = InMemoryTaskStore()
        val updateTaskStatus = UpdateTaskStatus(taskStore)
        val ioScheduler = Schedulers.io()
        val uploaderStateAggregator = UploaderStateAggregator(taskStore, Schedulers.computation())
        val uploader = UploaderImpl(
            updateTaskStatus,
            ioScheduler,
            taskStore,
            uploaderStateAggregator,
            api.uploadActionFactory()
        )

        kitten { url ->
            val observeTaskStatuses = uploader.status().test()
            val observeUploaderState = uploader.state().test()
            val task = url.file.asTask()
            val initialStatus = task.status

            uploader.enqueue(task)

            observeTaskStatuses.await(1, TimeUnit.SECONDS)
            val taskStatus = observeTaskStatuses.values()

            taskStatus shouldContain initialStatus
            taskStatus shouldContain initialStatus.sending(progress = 0)
            taskStatus shouldContain initialStatus.sending(progress = 6)
            taskStatus shouldContain initialStatus.sending(progress = 13)
            taskStatus shouldContain initialStatus.sending(progress = 19)
            taskStatus shouldContain initialStatus.sending(progress = 26)
            taskStatus shouldContain initialStatus.sending(progress = 33)
            taskStatus shouldContain initialStatus.sending(progress = 39)
            taskStatus shouldContain initialStatus.sending(progress = 46)
            taskStatus shouldContain initialStatus.sending(progress = 52)
            taskStatus shouldContain initialStatus.sending(progress = 59)
            taskStatus shouldContain initialStatus.sending(progress = 66)
            taskStatus shouldContain initialStatus.sending(progress = 72)
            taskStatus shouldContain initialStatus.sending(progress = 79)
            taskStatus shouldContain initialStatus.sending(progress = 85)
            taskStatus shouldContain initialStatus.sending(progress = 92)
            taskStatus shouldContain initialStatus.sending(progress = 99)
            taskStatus shouldContain initialStatus.sending(progress = 100)
            taskStatus shouldContain initialStatus.completed()

            taskStatus shouldContainSame listOf(
                initialStatus,
                initialStatus.sending(progress = 0),
                initialStatus.sending(progress = 6),
                initialStatus.sending(progress = 13),
                initialStatus.sending(progress = 19),
                initialStatus.sending(progress = 26),
                initialStatus.sending(progress = 33),
                initialStatus.sending(progress = 39),
                initialStatus.sending(progress = 46),
                initialStatus.sending(progress = 52),
                initialStatus.sending(progress = 59),
                initialStatus.sending(progress = 66),
                initialStatus.sending(progress = 72),
                initialStatus.sending(progress = 79),
                initialStatus.sending(progress = 85),
                initialStatus.sending(progress = 92),
                initialStatus.sending(progress = 99),
                initialStatus.sending(progress = 100),
                initialStatus.completed()
            )

            val uploaderStatus = observeUploaderState.values()
            uploaderStatus shouldContain Uploader.State(pending = 1)
            uploaderStatus shouldContain Uploader.State(sending = 1)
            uploaderStatus shouldContain Uploader.State(completed = 1)
            uploaderStatus shouldContainSame listOf(
                Uploader.State(pending = 1),
                Uploader.State(sending = 1),
                Uploader.State(completed = 1)
            )
        }
    }

    private fun kitten(test: (URL) -> Unit) = resource("kitten.jpg", test)

    private fun resource(name: String, test: (URL) -> Unit) {
        val urlClassLoader = FunctionalTests::class.java.classLoader as? URLClassLoader
        val resource = urlClassLoader?.findResource(name)
        checkNotNull(resource) { "Failed to resolve resource on path $name" }
        test(resource)
    }
}
