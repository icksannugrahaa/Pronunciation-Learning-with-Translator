package com.ninepm.english.learn.ui.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.ninepm.english.learn.R
import com.ninepm.english.learn.data.source.local.entity.UserEntity
import com.ninepm.english.learn.databinding.ActivityRegistrationBinding
import com.ninepm.english.learn.databinding.RegistrationContentDetailBinding
import com.ninepm.english.learn.ui.home.ViewModelFactory
import com.ninepm.english.learn.ui.verification.VerificationWaitActivity
import com.ninepm.english.learn.utils.MyUtils.Companion.showToast

class RegistrationActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: RegistrationContentDetailBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val registrationActivity = ActivityRegistrationBinding.inflate(layoutInflater)
        binding = registrationActivity.registrationContentDetail
        binding.btnActRegister.setOnClickListener(this)
        setSupportActionBar(registrationActivity.registrationToolbar)
        setContentView(registrationActivity.root)
        title = resources.getString(R.string.login)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]
    }

    private fun register() {
        with(binding) {
            when {
                TextUtils.isEmpty(regIdtEmail.text) -> {
                    regIdtEmail.error = resources.getString(R.string.email_error_empty_message)
                    return@with
                }
                TextUtils.isEmpty(regIdtUsername.text) -> {
                    regIdtUsername.error = resources.getString(R.string.username_error_empty_message)
                    return@with
                }
                TextUtils.isEmpty(regIdtPassword.text) -> {
                    regIdtPassword.error = resources.getString(R.string.password_error_empty_message)
                    return@with
                }
            }

            viewModel.userRegister(
                UserEntity(
                    email = regIdtEmail.text.toString(),
                    password = regIdtPassword.text.toString(),
                    username = regIdtUsername.text.toString()
                )
            ).observe(this@RegistrationActivity) {
                if (it) {
                    showToast(
                        "Registration success, please check email for activation!",
                        this@RegistrationActivity
                    )
                    Intent(this@RegistrationActivity, VerificationWaitActivity::class.java).apply {
                        this.putExtra(
                            VerificationWaitActivity.USER_EMAIL,
                            regIdtEmail.text.toString()
                        )
                        this.putExtra(
                            VerificationWaitActivity.USER_PASS,
                            regIdtPassword.text.toString()
                        )
                        startActivity(this)
                    }
                    finish()
                } else {
                    showToast("Register failed, please try again later!", this@RegistrationActivity)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_act_register -> {
                register()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}