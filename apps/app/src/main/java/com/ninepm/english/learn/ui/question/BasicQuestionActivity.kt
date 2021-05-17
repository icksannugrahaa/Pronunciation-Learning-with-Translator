package com.ninepm.english.learn.ui.question

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.ActivityQuestionBasicBinding
import com.ninepm.english.learn.databinding.QuestionBasicContentDetailBinding

class BasicQuestionActivity : AppCompatActivity() , View.OnClickListener {
    private lateinit var binding: QuestionBasicContentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val basicQuestionActivity = ActivityQuestionBasicBinding.inflate(layoutInflater)
        basicQuestionActivity.btnBack.setOnClickListener(this)
        binding = basicQuestionActivity.questionBasicContentDetail
        setSupportActionBar(basicQuestionActivity.questionBasicToolbar)
        setContentView(basicQuestionActivity.root)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_back -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.are_you_sure))
                    .setMessage(resources.getString(R.string.leave_message))
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                        dialog.dismiss()
                        finish()
                    }
                    .show()
            }
        }
    }
}