package com.ninepm.english.learn.firebase.storage

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import java.io.FileInputStream
import kotlin.coroutines.CoroutineContext


class FirebaseStorageConfig {
    companion object {
        //        private val storage = Firebase.storage("gs://9pm-bucket")
//        private val storage = FirebaseStorage.getInstance("gs://learn-9pm")
//        private val storageRef = storage.reference
//
//        val audioRef = storageRef.child("audio")

        fun setStorage(context: Context): Storage {
            var credentials = GoogleCredentials.fromStream(context.assets.open("learn-9pm.json"))
                .createScoped(arrayListOf("https://www.googleapis.com/auth/cloud-platform"))

            return StorageOptions.newBuilder().setCredentials(credentials).setProjectId("learn-9pm").build().service
        }

    }
}