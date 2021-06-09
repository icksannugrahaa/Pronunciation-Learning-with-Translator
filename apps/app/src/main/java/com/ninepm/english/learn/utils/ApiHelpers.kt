package com.ninepm.english.learn.utils

import android.annotation.SuppressLint
import com.ninepm.english.learn.data.source.remote.network.ApiConfig
import com.ninepm.english.learn.data.source.remote.response.ResponsePredict
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.nio.file.Paths

class ApiHelpers {
    @SuppressLint("NewApi")
    suspend fun getPredict(path: String): ResponsePredict {
        val filePath = Paths.get(path)
        val file = File(path)
        val fileBody: MultipartBody.Part = MultipartBody.Part.createFormData("file", filePath.fileName.toString(), file.asRequestBody("application/octet-stream".toMediaType()))
        val result = ApiConfig.provideApiService().getPredict(fileBody)

        return result
    }
}