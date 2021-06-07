package com.ninepm.english.learn.ui.verification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ninepm.english.learn.R
import com.ninepm.english.learn.data.source.local.entity.User
import com.ninepm.english.learn.databinding.ActivityVerificationWaitBinding
import com.ninepm.english.learn.ui.home.ViewModelFactory
import com.ninepm.english.learn.utils.MyUtils.Companion.loadGIFDrawable
import kotlinx.coroutines.*

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

        binding.imageView.loadGIFDrawable(R.drawable.anim_wait_hour, true)
        binding.imageView2.loadGIFDrawable(R.drawable.anim_wait_woman, true)

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