package com.ninepm.english.learn.data.source

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ninepm.english.learn.data.source.remote.RemoteDataSource
import com.ninepm.english.learn.utils.AppExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RemoteRepositoryImpl private constructor(
    private val remoteDataSource: RemoteDataSource,
    private val appExecutors: AppExecutors
) : RemoteRepository {

    companion object {
        @Volatile
        private var instance: RemoteRepositoryImpl? = null
        fun getInstance(remoteDataSource: RemoteDataSource, appExecutors: AppExecutors): RemoteRepositoryImpl =
            instance ?: synchronized(this) {
                instance ?: RemoteRepositoryImpl(remoteDataSource, appExecutors).apply { instance = this }
            }
    }

    override fun predictAudio(path: String, context: Context) : LiveData<String> {
        val result = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val data = remoteDataSource.predictAudio(path)
            Log.d("RESULT_PREDICT", data.value.toString())
            result.postValue(data.value?.word)
        }
        return result
    }
}