package com.olderworld.feature.uploader.data

import io.reactivex.rxjava3.core.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

internal interface Api {
    @Multipart
    @POST("/testUpload")
    fun upload(
        @Part("file") file: RequestBody,
        @Part("data") data: RequestBody
    ): Single<Response<ResponseBody>>
}
