package com.ninepm.english.learn.data.source.remote.network

import com.ninepm.english.learn.data.source.remote.response.ResponsePredict
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("predict")
    suspend fun getPredict(@Part file: MultipartBody.Part): ResponsePredict
}