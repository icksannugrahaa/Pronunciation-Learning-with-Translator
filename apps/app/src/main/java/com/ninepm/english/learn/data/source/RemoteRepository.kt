package com.ninepm.english.learn.data.source

import android.content.Context
import androidx.lifecycle.LiveData

interface RemoteRepository {
    fun predictAudio(path: String, context: Context) : LiveData<String>
}