package com.ninepm.english.learn.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.ActivityMainBinding
import com.ninepm.english.learn.ui.login.LoginActivity
import com.ninepm.english.learn.ui.question.BasicQuestionActivity
import com.ninepm.english.learn.utils.AnimationUtils.Companion.setAnimFlyUp
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    var database: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnTry.setOnClickListener {
            Intent(this, BasicQuestionActivity::class.java).apply {
                startActivity(this)
            }
        }
        binding.btnLogin.setOnClickListener {
            Intent(this, LoginActivity::class.java).apply {
                startActivity(this)
            }
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.child("profile")

        setAnim()
        checkLogin()
    }

    private fun checkLogin() {
        val user = auth.currentUser
        Log.d("user_login", user.toString())
        if(user != null) {
            val userReference = databaseReference?.child(user.uid)
            Log.d("user_data_email", user.email.toString())
            with(binding) {
                txtTitle.text = "Selamat Datang,"

                userReference?.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("user_data_email", snapshot.child("username").toString())
                        txtSubtitle.text = snapshot.child("username").value.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
                btnLogin.text = "Logout"
                btnLogin.setOnClickListener {
                    auth.signOut()
                    Intent(this@MainActivity, LoginActivity::class.java).apply {
                        startActivity(this)
                    }
                    finish()
                }
            }
        } else {
            with(binding) {
                txtTitle.text = resources.getString(R.string.let_start)
                txtSubtitle.text = resources.getString(R.string.login_to_save_your_progress)
                btnLogin.text = resources.getString(R.string.login)
            }
        }
    }

    private fun getBackgroundHeight(): Float {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return if(displayMetrics.widthPixels <= 720) {
            -1100F
        } else {
            -1470F
        }
    }

    private fun setAnim() {
        binding.apply {
            bgSplashscreen.animate().translationY(getBackgroundHeight()).setDuration(600).startDelay = 400
            txtIntroduce.animate().translationY(140F).alpha(0F).startDelay = 400
            GlobalScope.launch {
                startAnimDelay(600)
            }
        }
    }

    private suspend fun startAnimDelay(length: Long) = withContext(Dispatchers.Main) {
        delay(length)
        binding.apply {
            txtTitle.setAnimFlyUp(this@MainActivity)
            txtSubtitle.setAnimFlyUp(this@MainActivity)
            txtOr.setAnimFlyUp(this@MainActivity)
            btnTry.setAnimFlyUp(this@MainActivity)
            btnLogin.setAnimFlyUp(this@MainActivity)
            imgBg.setAnimFlyUp(this@MainActivity)
        }
    }
}