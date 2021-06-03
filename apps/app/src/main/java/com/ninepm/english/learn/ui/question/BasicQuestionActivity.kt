package com.ninepm.english.learn.ui.question

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.media.AudioFormat
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.ActivityQuestionBasicBinding
import com.ninepm.english.learn.databinding.QuestionBasicContentDetailBinding
import com.ninepm.english.learn.utils.MyUtils.Companion.showToast
import com.ninepm.english.learn.utils.RecorderService
import com.ninepm.english.learn.utils.TextToSpeechServices
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("UseCompatLoadingForDrawables")
class BasicQuestionActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: QuestionBasicContentDetailBinding
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var ttsService: TextToSpeechServices
    private lateinit var recorderService: RecorderService

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
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

        binding.btnActRecord.setOnClickListener(this)
        binding.imgAnswerPlay.setOnClickListener(this)

        ttsService = TextToSpeechServices()
        ttsService.setContext(this)

        val preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(PLAYER_STATE, "stop")
        editor.putString(RECORDER_STATE, "stop")
        editor.apply()
    }

    @RequiresApi(Build.VERSION_CODES.M)
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
                            startUpdate()
                        }
                    }
                }
            }
            R.id.btn_act_record -> {
                if (preferences.getString(RECORDER_STATE, "null").equals("stop")) {
                    val editor = preferences.edit()
                    editor.putString(RECORDER_STATE, "record")
                    editor.apply()

                    binding.btnActRecord.backgroundTintList = ColorStateList.valueOf(
                        resources.getColor(
                            R.color.red_200,
                            theme
                        )
                    )

                    showToast("Start record!", this)
                    recorderService.startRecording()
//                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
//                    mediaRecorder.setOutputFormat(AudioFormat.ENCODING_PCM_16BIT)
//                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
//                    mediaRecorder.setAudioChannels(AudioFormat.CHANNEL_IN_MONO)
//                    mediaRecorder.setAudioSamplingRate(22050)
//                    mediaRecorder.setOutputFile(path)
//                    mediaRecorder.prepare()
//                    mediaRecorder.start()

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

                    val regex = """a([bc]+)d?""".toRegex()
                    val matchResult = regex.find("abcb abbd")
//                    Log.d("fuzzy", StringUtils.difference())

                    showToast("Stop record!", this)
                    recorderService.stopRecording()
//                    mediaRecorder.stop()
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

    override fun onBackPressed() {
        showAlertExitDialog()
    }

    private fun startUpdate() {
        val preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        stopUpdates()
        job = scope.launch {
            while(ttsService.isSpeak()) {
                delay(1000)
            }
            if(!ttsService.isSpeak()) {
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

    private fun stopUpdates() {
        job?.cancel()
        job = null
    }

    public override fun onDestroy() {
        if (ttsService.isSpeak()) {
            ttsService.stop()
            ttsService.shutdown()
        }
        super.onDestroy()
    }
}