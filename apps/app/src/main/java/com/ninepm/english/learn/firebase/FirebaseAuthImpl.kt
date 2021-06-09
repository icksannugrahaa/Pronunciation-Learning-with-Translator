package com.ninepm.english.learn.firebase

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import com.ninepm.english.learn.data.source.local.entity.UserEntity
import com.ninepm.english.learn.firebase.auth.FirebaseAuthConfig
import com.ninepm.english.learn.firebase.auth.FirebaseAuthConfig.Companion.auth
import com.ninepm.english.learn.firebase.storage.FirebaseStorageConfig
import com.ninepm.english.learn.utils.AppExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Paths

class FirebaseAuthImpl private constructor(
    private val appExecutors: AppExecutors
) : FirebaseAuthRepo {
    private var database: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    private var uploadReference: StorageReference? = null

    companion object {
        @Volatile
        private var instance: FirebaseAuthImpl? = null
        fun getInstance(appExecutors: AppExecutors): FirebaseAuthImpl =
            instance ?: synchronized(this) {
                instance ?: FirebaseAuthImpl(appExecutors).apply { instance = this }
            }
    }

    override fun firebaseGetUser(): LiveData<UserEntity> {
        val user = MutableLiveData<UserEntity>()
        database = FirebaseAuthConfig.getFirebaseDBInstance()
        databaseReference = database?.reference?.child("profile")

        val currentUser = auth.currentUser

        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                val userReference = databaseReference?.child(currentUser.uid ?: "")

                userReference?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        user.postValue(
                            UserEntity(
                                uid = currentUser.uid,
                                username = snapshot.child("username").value.toString(),
                                email = currentUser.email,
                                profilePath = currentUser.photoUrl.toString(),
                                isVerified = currentUser.isEmailVerified
                            )
                        )
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("firebase_error", error.message)
                    }
                })
            } else {
                FirebaseAuthConfig.signOut()
            }
        } else {
            user.postValue(UserEntity())
        }

        Log.d("firebase_currentUser", auth.currentUser?.uid.toString())
//        Log.d("firebase_currentUser", user.value.toString())
        return user
    }

    override fun firebaseRegister(user: UserEntity): LiveData<Boolean> {
        val status = MutableLiveData<Boolean>()

        database = FirebaseAuthConfig.getFirebaseDBInstance()
        databaseReference = database?.reference?.child("profile")

        val currentUser = auth.currentUser

        if (currentUser != null) {
            status.postValue(false)
            FirebaseAuthConfig.signOut()
        } else {
            auth.createUserWithEmailAndPassword(user.email.toString(), user.password.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userC = auth.currentUser
                        val currentUserC = databaseReference?.child(userC!!.uid)
                        currentUserC?.child("username")?.setValue(user.username.toString())
                        userC?.sendEmailVerification()
                        status.postValue(true)
                    } else {
                        status.postValue(false)
                    }
                }
        }
        return status
    }

    override fun firebaseLogin(user: UserEntity): LiveData<String> {
        val status = MutableLiveData<String>()

        database = FirebaseAuthConfig.getFirebaseDBInstance()
        databaseReference = database?.reference?.child("profile")

        val currentUser = auth.currentUser

        if (currentUser != null) {
            status.postValue("false")
            FirebaseAuthConfig.signOut()
        } else {
            auth.signInWithEmailAndPassword(user.email.toString(), user.password.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (it.result?.user?.isEmailVerified!!) {
                            status.postValue("true")
                        } else {
                            status.postValue("need action")
                        }
                    } else {
                        status.postValue("false")
                    }
                }
        }
        Log.d("firebase_login_status", status.value.toString())
        return status
    }

    override fun firebaseCheckVerification(user: UserEntity): LiveData<Boolean> {
        val status = MutableLiveData<Boolean>()

        database = FirebaseAuthConfig.getFirebaseDBInstance()
        databaseReference = database?.reference?.child("profile")

        val currentUser = auth.currentUser

        if (currentUser != null) {
            status.postValue(auth.currentUser?.isEmailVerified)
            FirebaseAuthConfig.signOut()
        } else {
            auth.signInWithEmailAndPassword(user.email.toString(), user.password.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        status.postValue(it.result?.user?.isEmailVerified!!)
                    }
                }
        }
        return status
    }

//    @SuppressLint("NewApi")
//    override fun uploadFile(path: String, context: Context): LiveData<String> {
//        val result = MutableLiveData<String>()
//
//        val file = Paths.get(path)
//        val bucketName = "learn-9pm"
//        val blobName = file.fileName.toString()
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val storage = FirebaseStorageConfig.setStorage(context)
//            val bucket = storage.get(bucketName)
//                ?: error("Bucket $bucketName does not exist. You can create a new bucket with the command 'create <bucket>'.")
//
//            bucket.create(blobName, Files.readAllBytes(file))
//
//            if(bucket.exists()) {
//                result.postValue("success upload")
//            } else {
//                result.postValue("failed bucket not found")
//            }
//        }
//
//        return result
//    }

}