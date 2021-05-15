package com.ninepm.english.learn.ui.question

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.ActivityQuestionBasicBinding
import com.ninepm.english.learn.databinding.QuestionBasicContentDetailBinding

class BasicQuestionActivity : AppCompatActivity() , View.OnClickListener {
    private lateinit var binding: QuestionBasicContentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val basicQuestionActivity = ActivityQuestionBasicBinding.inflate(layoutInflater)
        basicQuestionActivity.txtBack.setOnClickListener(this)
        binding = basicQuestionActivity.questionBasicContentDetail
        setSupportActionBar(basicQuestionActivity.lessonToolbar)
        setContentView(basicQuestionActivity.root)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.txt_back -> {
                finish()
            }
        }
    }
}