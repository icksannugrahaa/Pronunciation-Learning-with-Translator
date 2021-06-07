package com.ninepm.english.learn.firebase.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ninepm.english.learn.data.source.local.entity.User

class FirebaseAuthConfig {
    companion object{
        lateinit var auth: FirebaseAuth

        fun getFirebaseDBInstance(): FirebaseDatabase {
            auth = FirebaseAuth.getInstance()
            return FirebaseDatabase.getInstance()
        }

        fun signOut() {
            auth.signOut()
        }
    }
}