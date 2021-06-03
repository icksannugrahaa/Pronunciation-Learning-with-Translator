package com.ninepm.english.learn.ui.question

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.github.squti.androidwaverecorder.RecorderState
import com.github.squti.androidwaverecorder.WaveRecorder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ninepm.english.learn.R
import com.ninepm.english.learn.data.source.local.entity.PlayerEntity
import com.ninepm.english.learn.databinding.ActivityQuestionBasicBinding
import com.ninepm.english.learn.databinding.QuestionBasicContentDetailBinding
import com.ninepm.english.learn.utils.PlayerPreferenceHelper

@SuppressLint("UseCompatLoadingForDrawables")
class BasicQuestionActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: QuestionBasicContentDetailBinding
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mediaPlayer: MediaPlayer

    var path = Environment.getExternalStorageDirectory().toString() + "/myrec.wav"
    var playerCurrentPosition = 0

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
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 111)

        mediaRecorder = MediaRecorder()
        mediaPlayer = MediaPlayer()

        binding.btnActRecord.setOnClickListener(this)
        binding.imgAnswerPlay.setOnClickListener(this)


        val preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(PLAYER_STATE, "stop")
        editor.putString(RECORDER_STATE, "stop")
        editor.apply()

        Log.d("player_state", preferences.getString(PLAYER_STATE, "null").toString())

        mediaPlayer.setOnCompletionListener {
            val editor = preferences.edit()
            editor.putString(PLAYER_STATE, "stop")
            editor.apply()

            mediaPlayer.stop()
            binding.imgAnswerPlay.setImageDrawable(resources.getDrawable(R.drawable.ic_volume_up_blue, theme))
        }
        mediaPlayer.setOnBufferingUpdateListener { mp, _ ->
            playerCurrentPosition = mp.currentPosition
        }
    }

    override fun onClick(v: View?) {
        val preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        when (v?.id) {
            R.id.btn_back -> {
                MaterialAlertDialogBuilder(this)
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
            R.id.img_answer_play -> {
                when (preferences.getString(PLAYER_STATE, "null")) {
                    "pause" -> {
                        val editor = preferences.edit()
                        editor.putString(PLAYER_STATE, "play")
                        editor.apply()

                        playerCurrentPosition = mediaPlayer.currentPosition
                        mediaPlayer.pause()
                        binding.imgAnswerPlay.setImageDrawable(resources.getDrawable(R.drawable.ic_play_blue, theme))
                    }
                    "play" -> {
                        val editor = preferences.edit()
                        editor.putString(PLAYER_STATE, "pause")
                        editor.apply()

                        mediaPlayer.seekTo(playerCurrentPosition)
                        mediaPlayer.start()
                        binding.imgAnswerPlay.setImageDrawable(resources.getDrawable(R.drawable.ic_pause_blue, theme))
                    }
                    "stop" -> {
                        val editor = preferences.edit()
                        editor.putString(PLAYER_STATE, "pause")
                        editor.apply()

                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(path)
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                        binding.imgAnswerPlay.setImageDrawable(resources.getDrawable(R.drawable.ic_pause_blue, theme))
                    }
                }
            }
            R.id.btn_act_record -> {
                if (preferences.getString(RECORDER_STATE, "null").equals("stop")) {
                    val editor = preferences.edit()
                    editor.putString(RECORDER_STATE, "record")
                    editor.apply()

                    Toast.makeText(this@BasicQuestionActivity, "start record!", Toast.LENGTH_LONG).show()
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                    mediaRecorder.setOutputFormat(AudioFormat.ENCODING_PCM_16BIT)
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    mediaRecorder.setAudioChannels(1)
                    mediaRecorder.setAudioEncodingBitRate(128000)
                    mediaRecorder.setAudioSamplingRate(48000)
                    mediaRecorder.setOutputFile(path)
                    mediaRecorder.prepare()
                    mediaRecorder.start()

                } else {
                    val editor = preferences.edit()
                    editor.putString(RECORDER_STATE, "stop")
                    editor.apply()

                    Toast.makeText(this@BasicQuestionActivity, "stop!", Toast.LENGTH_LONG).show()
                    mediaRecorder.stop()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
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
}