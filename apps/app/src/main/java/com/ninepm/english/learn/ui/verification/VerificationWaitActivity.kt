package com.ninepm.english.learn.ui.verification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ninepm.english.learn.R
import com.ninepm.english.learn.data.source.local.entity.User
import com.ninepm.english.learn.databinding.ActivityVerificationWaitBinding
import com.ninepm.english.learn.databinding.FragmentHomeBinding
import com.ninepm.english.learn.firebase.auth.FirebaseAuthConfig
import com.ninepm.english.learn.firebase.auth.FirebaseAuthConfig.Companion.auth
import com.ninepm.english.learn.ui.home.HomeViewModel
import com.ninepm.english.learn.ui.home.ViewModelFactory
import com.ninepm.english.learn.ui.login.LoginActivity
import com.ninepm.english.learn.ui.login.LoginViewModel
import com.ninepm.english.learn.utils.MyUtils.Companion.loadDrawable
import com.ninepm.english.learn.utils.MyUtils.Companion.loadImage
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive
import java.util.concurrent.TimeUnit

class VerificationWaitActivity : AppCompatActivity() {
    private lateinit var viewModel: VerificationViewModel
    private lateinit var binding: ActivityVerificationWaitBinding
    private val scope = MainScope()
    var job: Job? = null

    companion object {
        const val USER_EMAIL = "email"
        const val USER_PASS = "pass"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationWaitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[VerificationViewModel::class.java]

        binding.imageView.loadDrawable(R.drawable.anim_wait_hour)
        binding.imageView2.loadDrawable(R.drawable.anim_wait_woman)

//        Glide.with(this)
//            .load(R.drawable.anim_wait_woman)
//            .into(binding.imageView2)

//        Glide.with(this)
//            .load()
//            .into(binding.imageView)

        startUpdates()
    }

//    override fun onResume() {
//        super.onResume()
//        startUpdates()
//    }
//
    private fun startUpdates() {
        stopUpdates()
        job = scope.launch {
            var status = false
            val email = intent.getStringExtra(USER_EMAIL)!!
            val pass = intent.getStringExtra(USER_PASS)!!
            while(!status) {
                viewModel.getStatus(User(email = email, password = pass)).observe(
                    this@VerificationWaitActivity,
                    {
                        status = it
                        if (it) {
                            Glide.with(this@VerificationWaitActivity)
                                .load(R.drawable.anim_success)
                                .into(binding.imageView)
                            stopUpdates()
                            @Suppress("DEPRECATION")
                            Handler().postDelayed({
                                Intent(this@VerificationWaitActivity, VerificationSuccessActivity::class.java).apply {
                                    startActivity(this)
                                }
                                finish()
                            }, 3000)
                        }
                    })
                delay(10000)
            }
        }
    }

    private fun stopUpdates() {
        job?.cancel()
        job = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}