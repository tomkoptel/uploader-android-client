package com.olderworld.feature.uploader.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

internal fun OkHttpClient.Builder.verboseLogging() = addInterceptor(
    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
)

internal fun baseRetrofit(client: OkHttpClient): Retrofit.Builder = Retrofit.Builder()
    .client(client)
    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
    .baseUrl("https://api.stg.globekeeper.com/")

internal fun Retrofit.api(): Api = create(Api::class.java)

internal fun String.asFileMetadata() = """
    {
        "data": {
            "name": "$this"
        }
    }
""".trimIndent()
