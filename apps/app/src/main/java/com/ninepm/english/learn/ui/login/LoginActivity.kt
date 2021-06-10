package com.ninepm.english.learn.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.ViewModelProvider
import com.ninepm.english.learn.R
import com.ninepm.english.learn.data.source.local.entity.UserEntity
import com.ninepm.english.learn.databinding.ActivityLoginBinding
import com.ninepm.english.learn.databinding.LoginContentDetailBinding
import com.ninepm.english.learn.ui.home.ViewModelFactory
import com.ninepm.english.learn.ui.registration.RegistrationActivity
import com.ninepm.english.learn.ui.verification.VerificationWaitActivity
import com.ninepm.english.learn.utils.MyUtils.Companion.showToast

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginContentDetailBinding
    private lateinit var viewModel: LoginViewModel

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
            showToast("This Feature not available for now!", this@LoginActivity)
        }
        setSupportActionBar(loginActivity.loginToolbar)
        setContentView(loginActivity.root)
        title = resources.getString(R.string.login)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun login() {
        with(binding) {
            if (TextUtils.isEmpty(idtEmail.text)) {
                idtEmail.error = resources.getString(R.string.email_error_empty_message)
                return@with
            } else if (TextUtils.isEmpty(idtPassword.text)) {
                idtPassword.error = resources.getString(R.string.password_error_empty_message)
                return@with
            }

            viewModel.userLogin(
                UserEntity(
                    email = idtEmail.text.toString(),
                    password = idtPassword.text.toString()
                )
            ).observe(this@LoginActivity, {
                if (it == "true") {
                    showToast("Login Success!", this@LoginActivity)
                    finish()
                } else if(it == "need action") {
                    Intent(this@LoginActivity, VerificationWaitActivity::class.java).apply {
                        this.putExtra(VerificationWaitActivity.USER_EMAIL,idtEmail.text.toString())
                        this.putExtra(VerificationWaitActivity.USER_PASS,idtPassword.text.toString())
                        startActivity(this)
                    }
                    showToast("Login failed, please complete email verification first!", this@LoginActivity)
                } else {
                    showToast("Login failed, please try again later!", this@LoginActivity)
                }
            })
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}