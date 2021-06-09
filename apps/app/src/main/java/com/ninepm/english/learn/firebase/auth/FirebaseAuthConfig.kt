package com.ninepm.english.learn.firebase.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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