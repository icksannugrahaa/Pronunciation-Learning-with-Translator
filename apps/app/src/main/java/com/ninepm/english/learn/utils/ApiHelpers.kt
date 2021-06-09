package com.ninepm.english.learn.utils

import android.annotation.SuppressLint
import android.content.Context
import com.ninepm.english.learn.data.source.remote.network.ApiConfig
import com.ninepm.english.learn.data.source.remote.response.ResponsePredict
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.nio.file.Paths

class ApiHelpers {
    @SuppressLint("NewApi")
    suspend fun getPredict(context: Context, path: String): ResponsePredict {
        val file = File(path)
        val fileBody: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name.toString(), file.asRequestBody("application/octet-stream".toMediaType()))
        return ApiConfig.provideApiService().getPredict(fileBody)
    }
}