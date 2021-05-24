package com.ninepm.english.learn.utils

import android.content.Context
import android.widget.Toast

class MyUtils {
    companion object {
        fun showToast(message: String, context: Context) {
            Toast.makeText(context,message,Toast.LENGTH_LONG).show()
        }
    }
}