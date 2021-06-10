package com.ninepm.english.learn.ui.question

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ninepm.english.learn.data.source.RemoteRepositoryImpl

class QuestionViewModel(private val repository: RemoteRepositoryImpl) : ViewModel() {
    fun predictAudio(path: String, context: Context) : LiveData<String> = repository.predictAudio(path, context)
}