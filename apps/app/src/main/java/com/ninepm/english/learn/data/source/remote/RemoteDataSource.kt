package com.ninepm.english.learn.data.source.remote

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ninepm.english.learn.data.source.remote.response.ResponsePredict
import com.ninepm.english.learn.utils.ApiHelpers
import kotlinx.coroutines.*

class RemoteDataSource private constructor(private val helpers: ApiHelpers) {
    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null

        fun getInstance(helpers: ApiHelpers): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource(helpers).apply { instance = this }
            }
    }

    suspend fun predictAudio(context: Context, path: String): LiveData<ResponsePredict> {
        val result = MutableLiveData<ResponsePredict>()
        val response = GlobalScope.async {
            withContext(Dispatchers.Default) {
                val response = helpers.getPredict(context, path)
                result.postValue(response)
            }
        }
        response.await()
        return result
    }
}