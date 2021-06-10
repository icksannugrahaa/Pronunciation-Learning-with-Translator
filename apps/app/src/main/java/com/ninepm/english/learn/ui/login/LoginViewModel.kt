package com.ninepm.english.learn.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ninepm.english.learn.data.source.local.entity.UserEntity
import com.ninepm.english.learn.firebase.FirebaseAuthImpl

class LoginViewModel(private val repository: FirebaseAuthImpl) : ViewModel() {
    fun userLogin(user: UserEntity) : LiveData<String> = repository.firebaseLogin(user)
}