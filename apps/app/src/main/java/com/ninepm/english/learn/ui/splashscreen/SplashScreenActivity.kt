package com.ninepm.english.learn.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.ninepm.english.learn.databinding.ActivitySplashScreenBinding
import com.ninepm.english.learn.ui.main.MainActivity
import kotlinx.coroutines.*

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        GlobalScope.launch {
            delay(3000)
            withContext(Dispatchers.Main) {
                Intent(this@SplashScreenActivity, MainActivity::class.java).apply {
                    startActivity(this)
                }
                finish()
            }
        }
    }
}