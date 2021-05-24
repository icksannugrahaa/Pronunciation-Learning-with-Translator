package com.ninepm.english.learn.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.google.firebase.auth.FirebaseAuth
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.ActivityLoginBinding
import com.ninepm.english.learn.databinding.LoginContentDetailBinding
import com.ninepm.english.learn.ui.main.MainActivity
import com.ninepm.english.learn.ui.question.BasicQuestionActivity
import com.ninepm.english.learn.ui.registration.RegistrationActivity
import com.ninepm.english.learn.ui.verification.VerificationSuccessActivity
import com.ninepm.english.learn.ui.verification.VerificationWaitActivity
import com.ninepm.english.learn.utils.MyUtils.Companion.showToast

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginContentDetailBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loginActivity = ActivityLoginBinding.inflate(layoutInflater)
        binding = loginActivity.loginContentDetail
        binding.btnRegister.setOnClickListener {
            Intent(this, RegistrationActivity::class.java).apply {
                startActivity(this)
            }
        }
        binding.btnActLogin.setOnClickListener {
            login()
        }
        binding.btnActGsignin.setOnClickListener {
            Intent(this, VerificationWaitActivity::class.java).apply {
                startActivity(this)
            }
        }
        setSupportActionBar(loginActivity.loginToolbar)
        setContentView(loginActivity.root)
        title = resources.getString(R.string.login)

        auth = FirebaseAuth.getInstance()
    }

    private fun login() {
        with(binding) {
            if(TextUtils.isEmpty(idtUsername.text)) {
                idtUsername.error = resources.getString(R.string.username_error_empty_message)
                return@with
            } else if(TextUtils.isEmpty(idtPassword.text)) {
                idtPassword.error = resources.getString(R.string.password_error_empty_message)
                return@with
            }

            auth.signInWithEmailAndPassword(idtUsername.text.toString(), idtPassword.text.toString())
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            showToast("Login failed, please try again later!",this@LoginActivity)
                        }
                    }
        }
    }
}