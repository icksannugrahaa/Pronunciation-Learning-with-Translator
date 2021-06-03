package com.ninepm.english.learn.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityHistoryBinding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(activityHistoryBinding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "History"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}