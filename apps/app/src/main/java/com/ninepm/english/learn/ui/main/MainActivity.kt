package com.ninepm.english.learn.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.ActivityMainBinding
import com.ninepm.english.learn.ui.login.LoginActivity
import com.ninepm.english.learn.utils.AnimationUtils.Companion.setAnimFlyUp
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAnim()
        binding.btnTry.setOnClickListener {
            Intent(this, LessonActivity::class.java).apply {
                startActivity(this)
            }
        }
        binding.btnLogin.setOnClickListener {
            Intent(this, LoginActivity::class.java).apply {
                startActivity(this)
            }
        }
    }
    private fun getBackgroundHeight(): Float {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return if(displayMetrics.widthPixels <= 720) {
            -1100F
        } else {
            -1470F
        }
    }
    private fun setAnim() {
        binding.apply {
            bgSplashscreen.animate().translationY(getBackgroundHeight()).setDuration(600).startDelay = 400
            txtIntroduce.animate().translationY(140F).alpha(0F).startDelay = 400
            GlobalScope.launch {
                startAnimDelay(600)
            }
        }
    }
    private suspend fun startAnimDelay(length: Long) = withContext(Dispatchers.Main) {
        delay(length)
        binding.apply {
            txtTitle.setAnimFlyUp(this@MainActivity)
            txtSubtitle.setAnimFlyUp(this@MainActivity)
            txtOr.setAnimFlyUp(this@MainActivity)
            btnTry.setAnimFlyUp(this@MainActivity)
            btnLogin.setAnimFlyUp(this@MainActivity)
            imgBg.setAnimFlyUp(this@MainActivity)
        }
    }
}