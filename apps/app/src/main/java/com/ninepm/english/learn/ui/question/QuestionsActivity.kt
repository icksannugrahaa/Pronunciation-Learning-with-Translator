package com.ninepm.english.learn.ui.question

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.ActivityQuestionsBinding

class QuestionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuestionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "List Questions"

        binding.itemQuestion.cardQuestions.setOnClickListener {
            Intent(this, BasicQuestionActivity::class.java).apply {
                startActivity(this)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}