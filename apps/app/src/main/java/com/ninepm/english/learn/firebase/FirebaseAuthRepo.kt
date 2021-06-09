package com.ninepm.english.learn.firebase

import android.content.Context
import androidx.lifecycle.LiveData
import com.ninepm.english.learn.data.source.local.entity.UserEntity

interface FirebaseAuthRepo {
    fun firebaseGetUser(): LiveData<UserEntity>
    fun firebaseRegister(user: UserEntity): LiveData<Boolean>
    fun firebaseLogin(user: UserEntity): LiveData<String>
    fun firebaseCheckVerification(user: UserEntity): LiveData<Boolean>
//    fun uploadFile(path: String, context: Context): LiveData<String>
}