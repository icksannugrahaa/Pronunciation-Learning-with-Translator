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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.ActivityQuestionBasicBinding
import com.ninepm.english.learn.databinding.CountdownDialogBinding
import com.ninepm.english.learn.databinding.QuestionBasicContentDetailBinding
import com.ninepm.english.learn.ui.question.ViewModelFactory
import com.ninepm.english.learn.utils.DialogUtils.showAlertExitDialog
import com.ninepm.english.learn.utils.DialogUtils.showNotificationDialog
import com.ninepm.english.learn.utils.MyUtils.Companion.getRandomString
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
import java.lang.StringBuilder


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
    private lateinit var viewModel: QuestionViewModel

    var path = StringBuilder().append("${Environment.getExternalStorageDirectory()}").toString()
    var filename: String? = null
    private val scope = MainScope()
    var job: Job? = null

    companion object {
        const val PREFS_NAME = "MEDIA_RECORDER_STATE"
        private const val RECORDER_STATE = "state"
        private const val PLAYER_STATE = "state"
        private const val AUDIO_ID = "id"
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
        timerService = TimerService()
        ttsService = TextToSpeechServices()
        ttsService.setContext(this)
        val preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val factory = ViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[QuestionViewModel::class.java]

        binding.btnActRecord.setOnClickListener(this)
        binding.imgAnswerPlay.setOnClickListener(this)

        if (preferences.getString(AUDIO_ID, null).equals(null)) {
            filename = StringBuilder().append("${getRandomString(20)}.wav").toString()
            editor.putString(AUDIO_ID, filename)
        } else {
            filename = preferences.getString(AUDIO_ID, null)
        }

        recorderService.WavRecorder(path, filename)

        editor.putString(PLAYER_STATE, "stop")
        editor.putString(RECORDER_STATE, "stop")
        editor.apply()
    }

    override fun onClick(v: View?) {
        val preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(binding) {
            when (v?.id) {
                R.id.btn_back -> {
                    showAlertExitDialog(this@BasicQuestionActivity, this@BasicQuestionActivity)
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

                                ttsService.speakText(tvLessonText.text.toString())
                                imgAnswerPlay.setImageDrawable(
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

                        btnActRecord.backgroundTintList = ColorStateList.valueOf(
                            resources.getColor(
                                R.color.red_200,
                                theme
                            )
                        )
                        startUpdateRecording()

                    } else {
                        val dialog = Dialog(this@BasicQuestionActivity)
                        val editor = preferences.edit()
                        editor.putString(RECORDER_STATE, "stop")
                        editor.apply()

                        btnActRecord.backgroundTintList = ColorStateList.valueOf(
                            resources.getColor(
                                R.color.green_200,
                                theme
                            )
                        )
                        recorderService.stopRecording()
                        showNotificationDialog(
                            dialog,
                            R.drawable.anim_wait_hour,
                            resources.getString(R.string.review),
                            true,
                            this@BasicQuestionActivity
                        )
                        viewModel.predictAudio(
                            StringBuilder().append("$path/$filename").toString(),
                            this@BasicQuestionActivity
                        ).observe(this@BasicQuestionActivity, { word ->
                            val ratio = FuzzySearch.ratio(
                                tvLessonText.text.toString(),
                                word
                            ).toString()

                            tvProgressBar.text = StringBuilder().append("$ratio%")
                            progressBarHorizontal.progress = ratio.toInt()
                            tvLessonCorrection.text = word

                            showNotificationDialog(
                                dialog,
                                R.drawable.anim_wait_hour,
                                resources.getString(R.string.review),
                                false,
                                this@BasicQuestionActivity
                            )
                        })
                        showTimeCountDown(false)
                        stopUpdates()
                    }
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

    override fun onBackPressed() {
        showAlertExitDialog(this, this)
    }

    private fun startUpdateRecording() {
        val preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val dialog = Dialog(this)
        stopUpdates()
        Log.d("STATUS_RECORD", preferences.getString(RECORDER_STATE, "null").toString())
        if (preferences.getString(RECORDER_STATE, "null").equals("waiting")) {
            job = scope.launch {
                showNotificationDialog(
                    dialog,
                    R.drawable.countdown,
                    resources.getString(R.string.getting_ready),
                    true,
                    this@BasicQuestionActivity
                )
                delay(5000)
                showNotificationDialog(
                    dialog,
                    R.drawable.countdown,
                    resources.getString(R.string.getting_ready),
                    false,
                    this@BasicQuestionActivity
                )

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

                while (time > 0) {
                    binding.progressBarTime.progress = time * 10
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
                showNotificationDialog(
                    dialog,
                    R.drawable.countdown_end,
                    resources.getString(R.string.time_is_up),
                    true,
                    this@BasicQuestionActivity
                )
                recorderService.stopRecording()
                delay(5000)
                showNotificationDialog(
                    dialog,
                    R.drawable.countdown_end,
                    resources.getString(R.string.time_is_up),
                    true,
                    this@BasicQuestionActivity
                )

                showNotificationDialog(
                    dialog,
                    R.drawable.anim_wait_hour,
                    resources.getString(R.string.review),
                    true,
                    this@BasicQuestionActivity
                )
                viewModel.predictAudio(
                    StringBuilder().append("$path/$filename").toString(),
                    this@BasicQuestionActivity
                ).observe(this@BasicQuestionActivity, { word ->
                    val ratio = FuzzySearch.ratio(
                        binding.tvLessonText.text.toString(),
                        word
                    ).toString()

                    binding.tvProgressBar.text = StringBuilder().append("$ratio%")
                    binding.progressBarHorizontal.progress = ratio.toInt()
                    binding.tvLessonCorrection.text = word

                    showNotificationDialog(
                        dialog,
                        R.drawable.anim_wait_hour,
                        resources.getString(R.string.review),
                        false,
                        this@BasicQuestionActivity
                    )
                })
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
        if (state) {
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