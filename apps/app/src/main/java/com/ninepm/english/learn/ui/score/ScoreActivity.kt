package com.ninepm.english.learn.ui.score

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ninepm.english.learn.R

class ScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Score"
    }
}