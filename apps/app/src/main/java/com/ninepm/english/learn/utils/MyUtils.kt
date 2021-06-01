package com.ninepm.english.learn.utils

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ninepm.english.learn.R
import com.ninepm.english.learn.utils.MyUtils.Companion.loadImage

class MyUtils {
    companion object {
        fun showToast(message: String, context: Context) {
            Toast.makeText(context,message,Toast.LENGTH_LONG).show()
        }
        fun ImageView.loadImage(url: String?) {
            Glide.with(this.context)
                .load(url)
                .apply(
                    RequestOptions.placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.user_default)
                )
                .into(this)
        }
        fun ImageView.loadDrawable(id: Int) {
            Glide.with(this.context)
                .load(id)
                .into(this)
        }
    }
}