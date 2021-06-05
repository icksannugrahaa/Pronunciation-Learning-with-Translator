package com.ninepm.english.learn.data.source.local.entity

data class User(
    val uid: String? = "user_id",
    val username: String? = "username",
    val email: String? = "email",
    val password: String? = "password",
    val profilePath: String? = "profile_path",
    val isVerified: Boolean? = false
)
