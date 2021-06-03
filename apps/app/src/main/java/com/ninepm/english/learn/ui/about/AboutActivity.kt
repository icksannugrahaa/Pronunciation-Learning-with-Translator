package com.ninepm.english.learn.ui.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityAboutBinding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(activityAboutBinding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "About"

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}