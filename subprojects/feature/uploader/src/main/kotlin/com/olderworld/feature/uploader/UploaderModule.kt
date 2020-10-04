package com.olderworld.feature.uploader

import com.olderworld.feature.uploader.data.Api
import com.olderworld.feature.uploader.data.api
import com.olderworld.feature.uploader.data.baseRetrofit
import com.olderworld.feature.uploader.data.uploadActionFactory
import com.olderworld.feature.uploader.data.verboseLogging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object UploaderModule {
    @Singleton
    @Provides
    fun taskStore(): TaskStore = InMemoryTaskStore()

    @Singleton
    @Provides
    fun uploader(
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
            api.uploadActionFactory()
        )
    }

    private val api: Api
        get() {
            return OkHttpClient.Builder()
                .verboseLogging()
                .writeTimeout(1L, TimeUnit.MINUTES)
                .readTimeout(1L, TimeUnit.MINUTES)
                .build().let {
                    baseRetrofit(it).build()
                }.api()
        }
}
