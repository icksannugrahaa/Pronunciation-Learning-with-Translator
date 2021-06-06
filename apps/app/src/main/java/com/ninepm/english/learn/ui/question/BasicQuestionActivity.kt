package com.ninepm.english.learn.ui.question

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.ActivityQuestionBasicBinding
import com.ninepm.english.learn.databinding.CountdownDialogBinding
import com.ninepm.english.learn.databinding.QuestionBasicContentDetailBinding
import com.ninepm.english.learn.utils.MyUtils.Companion.loadGIFDrawable
import com.ninepm.english.learn.utils.MyUtils.Companion.showToast
import com.ninepm.english.learn.utils.RecorderService
import com.ninepm.english.learn.utils.TextToSpeechServices
import com.ninepm.english.learn.utils.TimerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.io.File


@SuppressLint("UseCompatLoadingForDrawables")
@RequiresApi(Build.VERSION_CODES.M)
class BasicQuestionActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: QuestionBasicContentDetailBinding
    private lateinit var bindingCountDownLayout: CountdownDialogBinding
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var ttsService: TextToSpeechServices
    private lateinit var recorderService: RecorderService
    private lateinit var timerService: TimerService

    var path = Environment.getExternalStorageDirectory().toString() + "/record.wav"
    private val scope = MainScope()
    var job: Job? = null

    companion object {
        const val PREFS_NAME = "MEDIA_RECORDER_STATE"
        private const val RECORDER_STATE = "state"
        private const val PLAYER_STATE = "state"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val basicQuestionActivity = ActivityQuestionBasicBinding.inflate(layoutInflater)
        basicQuestionActivity.btnBack.setOnClickListener(this)
        binding = basicQuestionActivity.questionBasicContentDetail
        setSupportActionBar(basicQuestionActivity.questionBasicToolbar)
        setContentView(basicQuestionActivity.root)
        bindingCountDownLayout = CountdownDialogBinding.inflate(layoutInflater, binding.root, false)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        )
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 111
            )

        mediaRecorder = MediaRecorder()
        mediaPlayer = MediaPlayer()
        recorderService = RecorderService()
        recorderService.WavRecorder(path)
        timerService = TimerService()

        binding.btnActRecord.setOnClickListener(this)
        binding.imgAnswerPlay.setOnClickListener(this)

        ttsService = TextToSpeechServices()
        ttsService.setContext(this)

        val preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(PLAYER_STATE, "stop")
        editor.putString(RECORDER_STATE, "stop")
        editor.apply()

//        Log.d(
//            "search_fuzzy", (
//                    FuzzySearch.ratio(
//                        "The paper on which the printing is to be done is a necessary part of our subject",
//                        "the paper on which the printing is to be done is a necessary part of ou ou subject"
//                    )
//                    ).toString()
//        )
    }

    override fun onClick(v: View?) {
        val preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        when (v?.id) {
            R.id.btn_back -> {
                showAlertExitDialog()
            }
            R.id.img_answer_play -> {
                val file = File(path)
                if (file.exists()) {
                    when (preferences.getString(PLAYER_STATE, "null")) {
                        "play" -> {
                            val editor = preferences.edit()
                            editor.putString(PLAYER_STATE, "stop")
                            editor.apply()

                            ttsService.stop()
                        }
                        "stop" -> {
                            val editor = preferences.edit()
                            editor.putString(PLAYER_STATE, "play")
                            editor.apply()

                            ttsService.speakText(binding.tvLessonText.text.toString())
                            binding.imgAnswerPlay.setImageDrawable(
                                resources.getDrawable(
                                    R.drawable.ic_stop_blue,
                                    theme
                                )
                            )
                            startUpdateTTS()
                        }
                    }
                }
            }
            R.id.btn_act_record -> {
                if (preferences.getString(RECORDER_STATE, "null").equals("stop")) {
                    val editor = preferences.edit()
                    editor.putString(RECORDER_STATE, "waiting")
                    editor.apply()

                    binding.btnActRecord.backgroundTintList = ColorStateList.valueOf(
                        resources.getColor(
                            R.color.red_200,
                            theme
                        )
                    )
                    startUpdateRecording()

                } else {
                    val editor = preferences.edit()
                    editor.putString(RECORDER_STATE, "stop")
                    editor.apply()

                    binding.btnActRecord.backgroundTintList = ColorStateList.valueOf(
                        resources.getColor(
                            R.color.green_200,
                            theme
                        )
                    )
                    recorderService.stopRecording()
                    showTimeCountDown(false)
                    stopUpdates()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            showToast("Permission granted!", this)
    }

    private fun showAlertExitDialog() {
        MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setTitle(resources.getString(R.string.are_you_sure))
            .setMessage(resources.getString(R.string.leave_message))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

    private fun showNotifDialog(dialog: Dialog, source: Int, title: String, state: Boolean) {
        if(!state) {
            dialog.dismiss()
        } else {
            dialog.setContentView(R.layout.countdown_dialog)
            dialog.findViewById<ImageView>(R.id.img_countdown).loadGIFDrawable(source, false)
            dialog.findViewById<TextView>(R.id.tv_dialog_title).text = title
            dialog.window?.setBackgroundDrawable(resources.getDrawable(R.color.transparent, theme))
            dialog.create()
            dialog.show()
        }
        Log.d("STATUS_DIALOG", dialog.isShowing.toString())
    }

    override fun onBackPressed() {
        showAlertExitDialog()
    }

    private fun startUpdateRecording() {
        val preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val dialog = Dialog(this)
        stopUpdates()
        Log.d("STATUS_RECORD", preferences.getString(RECORDER_STATE, "null").toString())
        if (preferences.getString(RECORDER_STATE, "null").equals("waiting")) {
            job = scope.launch {
                showNotifDialog(dialog, R.drawable.countdown, resources.getString(R.string.getting_ready), true)
                delay(5000)
                showNotifDialog(dialog, R.drawable.countdown, resources.getString(R.string.getting_ready), false)

                val editor = preferences.edit()
                editor.putString(RECORDER_STATE, "recording")
                editor.apply()
                startUpdateRecording()
            }

        } else if (preferences.getString(RECORDER_STATE, "null").equals("recording")) {
            recorderService.startRecording()
            showTimeCountDown(true)
            var time = 10

            job = scope.launch {

                while(time > 0) {
                    binding.progressBarTime.progress = time*10
                    binding.tvProgressTime.text = time.toString()
                    delay(1000)
                    time -= 1
                }

                showTimeCountDown(false)
                val editor = preferences.edit()
                editor.putString(RECORDER_STATE, "stop")
                editor.apply()
                binding.btnActRecord.backgroundTintList = ColorStateList.valueOf(
                    resources.getColor(
                        R.color.green_200,
                        theme
                    )
                )
                showNotifDialog(dialog, R.drawable.countdown_end, resources.getString(R.string.time_is_up),true)
                recorderService.stopRecording()
                delay(5000)
                showNotifDialog(dialog, R.drawable.countdown_end, resources.getString(R.string.time_is_up), false)
                stopUpdates()
            }
        }
    }

    private fun startUpdateTTS() {
        val preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        stopUpdates()
        job = scope.launch {
            while (ttsService.isSpeak()) {
                delay(1000)
            }
            if (!ttsService.isSpeak()) {
                binding.imgAnswerPlay.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_volume_up_blue,
                        theme
                    )
                )

                val editor = preferences.edit()
                editor.putString(PLAYER_STATE, "stop")
                editor.apply()

                stopUpdates()
            }
        }
    }

    private fun showTimeCountDown(state: Boolean) {
        if(state) {
            binding.progressBarTime.visibility = View.VISIBLE
            binding.tvProgressTime.visibility = View.VISIBLE
        } else {
            binding.progressBarTime.visibility = View.GONE
            binding.tvProgressTime.visibility = View.GONE
        }
    }

    private fun stopUpdates() {
        job?.cancel()
        job = null
    }

    public override fun onDestroy() {
        recorderService.stopRecording()
        stopUpdates()
        if (ttsService.isSpeak()) {
            ttsService.stop()
            ttsService.shutdown()
        }
        super.onDestroy()
    }
}