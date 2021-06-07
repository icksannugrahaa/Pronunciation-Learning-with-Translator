package com.ninepm.english.learn.firebase

import androidx.lifecycle.LiveData
import com.ninepm.english.learn.data.source.local.entity.User

interface FirebaseAuthRepo {
    fun firebaseGetUser(): LiveData<User>
    fun firebaseRegister(user: User): LiveData<Boolean>
    fun firebaseLogin(user: User): LiveData<String>
    fun firebaseCheckVerification(user: User): LiveData<Boolean>
}