package com.ninepm.english.learn.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ninepm.english.learn.data.source.local.entity.User
import com.ninepm.english.learn.firebase.FirebaseAuthImpl

class HomeViewModel(private val repository: FirebaseAuthImpl) : ViewModel() {
    fun getUser() : LiveData<User> = repository.firebaseGetUser()
}