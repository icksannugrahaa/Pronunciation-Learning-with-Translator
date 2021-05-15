package com.ninepm.english.learn.utils

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import com.ninepm.english.learn.R

class AnimationUtils {
    companion object {
        fun View.setAnimFlyUp(context: Context) {
            this.visibility = View.VISIBLE
            startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.fly_in
                )
            )
        }
    }
}