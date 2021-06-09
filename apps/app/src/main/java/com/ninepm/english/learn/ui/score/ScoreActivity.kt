package com.ninepm.english.learn.ui.score

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ninepm.english.learn.R
import com.ninepm.english.learn.data.source.local.entity.PredictEntity
import com.ninepm.english.learn.databinding.ActivityScoreBinding
import com.ninepm.english.learn.ui.login.LoginActivity
import com.ninepm.english.learn.utils.DialogUtils
import com.ninepm.english.learn.utils.MyUtils
import com.ninepm.english.learn.utils.MyUtils.Companion.showToast
import java.lang.StringBuilder

class ScoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScoreBinding

    companion object {
        val PREDICT_DATA = "predict_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Score"

        val predictData = intent.getParcelableExtra<PredictEntity>(PREDICT_DATA)

        binding.txtValueScore.text = StringBuilder().append("${predictData?.score.toString()}%")
        binding.btnCancelScore.setOnClickListener {
            DialogUtils.showAlertExitDialog(this, this)
        }
        binding.btnSaveScore.setOnClickListener {
            showToast("This Feature not available for now!",this)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}