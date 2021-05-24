package com.ninepm.english.learn.ui.registration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.ActivityRegistrationBinding
import com.ninepm.english.learn.databinding.RegistrationContentDetailBinding
import com.ninepm.english.learn.utils.MyUtils.Companion.showToast

class RegistrationActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: RegistrationContentDetailBinding
    lateinit var auth: FirebaseAuth
    var database: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val registrationActivity = ActivityRegistrationBinding.inflate(layoutInflater)
        binding = registrationActivity.registrationContentDetail
        binding.btnActRegister.setOnClickListener(this)
        setSupportActionBar(registrationActivity.registrationToolbar)
        setContentView(registrationActivity.root)
        title = resources.getString(R.string.login)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.child("profile")

    }

    private fun register() {
        with(binding) {
            when {
                TextUtils.isEmpty(regIdtEmail.text) -> {
                    regIdtEmail.error = resources.getString(R.string.email_error_empty_message)
                    return@with
                }
                TextUtils.isEmpty(regIdtUsername.text) -> {
                    regIdtUsername.error = resources.getString(R.string.username_error_empty_message)
                    return@with
                }
                TextUtils.isEmpty(regIdtPassword.text) -> {
                    regIdtPassword.error = resources.getString(R.string.password_error_empty_message)
                    return@with
                }
            }

            auth.createUserWithEmailAndPassword(regIdtEmail.text.toString(), regIdtPassword.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        val user = auth.currentUser
                        val currentUser = databaseReference?.child(user!!.uid)
                        currentUser?.child("username")?.setValue(regIdtUsername.text.toString())
                        showToast("Registration success, please check email for activation!",this@RegistrationActivity)
                        finish()
                    } else {
                        showToast("Registration failed, please try again!",this@RegistrationActivity)
                    }
                }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_act_register -> {
                register()
            }
        }
    }
}