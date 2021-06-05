package com.ninepm.english.learn.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.ninepm.english.learn.data.source.local.entity.User
import com.ninepm.english.learn.firebase.auth.FirebaseAuthConfig
import com.ninepm.english.learn.firebase.auth.FirebaseAuthConfig.Companion.auth
import com.ninepm.english.learn.utils.AppExecutors

class FirebaseAuthImpl private constructor(
    private val appExecutors: AppExecutors
) : FirebaseAuthRepo {
    var database: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null

    companion object {
        @Volatile
        private var instance: FirebaseAuthImpl? = null
        fun getInstance(appExecutors: AppExecutors): FirebaseAuthImpl =
            instance ?: synchronized(this) {
                instance ?: FirebaseAuthImpl(appExecutors).apply { instance = this }
            }
    }

    override fun firebaseGetUser(): LiveData<User> {
        val user = MutableLiveData<User>()
        database = FirebaseAuthConfig.getFirebaseDBInstance()
        databaseReference = database?.reference?.child("profile")

        val currentUser = auth.currentUser

        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                val userReference = databaseReference?.child(currentUser.uid ?: "")

                userReference?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        user.postValue(
                            User(
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
            user.postValue(User())
        }

        Log.d("firebase_currentUser", auth.currentUser?.uid.toString())
//        Log.d("firebase_currentUser", user.value.toString())
        return user
    }

    override fun firebaseRegister(user: User): LiveData<Boolean> {
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

    override fun firebaseLogin(user: User): LiveData<String> {
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

    override fun firebaseCheckVerification(user: User): LiveData<Boolean> {
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

}