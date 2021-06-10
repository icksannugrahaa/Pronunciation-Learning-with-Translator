package com.ninepm.english.learn.ui.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ninepm.english.learn.data.source.RemoteRepositoryImpl
import com.ninepm.english.learn.di.Injection

class ViewModelFactory private constructor(private val repository: RemoteRepositoryImpl) : ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.providePredict()).apply {
                    instance = this
                }
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(QuestionViewModel::class.java) -> {
                QuestionViewModel(repository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    }
}