package com.olderworld.feature.uploader

import android.app.Application
import com.olderworld.feature.uploader.data.Api
import com.olderworld.feature.uploader.data.api
import com.olderworld.feature.uploader.data.baseRetrofit
import com.olderworld.feature.uploader.data.uploadActionFactory
import com.olderworld.feature.uploader.data.verboseLogging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal class UploaderModule {
    @Singleton
    @Provides
    fun appTaskStore(): TaskStoreObserver = InMemoryTaskStore().observer()

    @Provides
    fun TaskStoreObserver.taskStore(): TaskStore = this

    @Provides
    fun TaskStoreObserver.rxTasks(): RxTasks = this

    @Singleton
    @Provides
    fun uploader(
        application: Application,
        updateTaskStatus: UpdateTaskStatus,
        taskStore: TaskStore
    ): Uploader {
        val ioScheduler = Schedulers.io()
        val uploaderStateAggregator = UploaderStateAggregator(taskStore, Schedulers.computation())
        return UploaderImpl(
            updateTaskStatus,
            ioScheduler,
            taskStore,
            uploaderStateAggregator,
            api.uploadActionFactory(application)
        )
    }

    private val api: Api
        get() {
            return OkHttpClient.Builder()
                .apply {
                    if (BuildConfig.DEBUG) verboseLogging()
                }
                .writeTimeout(1L, TimeUnit.MINUTES)
                .readTimeout(1L, TimeUnit.MINUTES)
                .build().let {
                    baseRetrofit(it).build()
                }.api()
        }
}
