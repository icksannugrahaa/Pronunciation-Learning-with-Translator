package com.ninepm.english.learn.ui.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ninepm.english.learn.data.source.local.entity.UserEntity
import com.ninepm.english.learn.firebase.FirebaseAuthImpl

class RegisterViewModel(private val repository: FirebaseAuthImpl) : ViewModel() {
    fun userRegister(user: UserEntity) : LiveData<Boolean> = repository.firebaseRegister(user)
}