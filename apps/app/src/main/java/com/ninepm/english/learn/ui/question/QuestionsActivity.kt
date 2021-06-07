package com.ninepm.english.learn.ui.question

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ninepm.english.learn.R

class QuestionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        setTitle("Questions")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}