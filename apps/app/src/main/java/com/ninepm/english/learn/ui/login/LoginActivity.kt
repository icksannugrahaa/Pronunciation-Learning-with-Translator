package com.ninepm.english.learn.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.ActivityLoginBinding
import com.ninepm.english.learn.databinding.LoginContentDetailBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginContentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loginActivity = ActivityLoginBinding.inflate(layoutInflater)
        binding = loginActivity.loginContentDetail
        setSupportActionBar(loginActivity.lessonToolbar)
        setContentView(loginActivity.root)
        title = resources.getString(R.string.login)
    }
}