package com.ninepm.english.learn.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ninepm.english.learn.R
import com.ninepm.english.learn.utils.MyUtils.Companion.loadGIFDrawable

object DialogUtils {
    fun showAlertExitDialog(context: Context, activity: Activity) {
        MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setTitle(context.resources.getString(R.string.are_you_sure))
            .setMessage(context.resources.getString(R.string.leave_message))
            .setNegativeButton(context.resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(context.resources.getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                activity.finish()
            }
            .show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun showNotificationDialog(dialog: Dialog, source: Int?, title: String?, state: Boolean, context: Context) {
        if(!state) {
            dialog.dismiss()
        } else {
            dialog.setContentView(R.layout.countdown_dialog)
            dialog.findViewById<ImageView>(R.id.img_countdown).loadGIFDrawable(source!!, false)
            dialog.findViewById<TextView>(R.id.tv_dialog_title).text = title
            dialog.window?.setBackgroundDrawable(context.resources?.getDrawable(R.color.transparent, context.theme))
            dialog.create()
            dialog.show()
        }
    }
}