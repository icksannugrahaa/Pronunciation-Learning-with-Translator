package com.ninepm.english.learn.ui.verification

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ninepm.english.learn.data.source.local.entity.User
import com.ninepm.english.learn.firebase.FirebaseAuthImpl

class VerificationViewModel(private val repository: FirebaseAuthImpl) : ViewModel() {
    fun getStatus(user: User) : LiveData<Boolean> = repository.firebaseCheckVerification(user)
}