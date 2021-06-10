package com.ninepm.english.learn.ui.verification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.ActivityVerificationSuccessBinding

class VerificationSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerificationSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .load(R.drawable.anim_success_verification)
            .into(binding.imageView4)

        binding.btnLoginSv.setOnClickListener {
            finish()
        }
    }
}