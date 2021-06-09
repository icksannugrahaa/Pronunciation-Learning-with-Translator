package com.ninepm.english.learn.data.source.local.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PredictEntity(
    val id: String? = null,
    val word: String? = null,
    val score: Int? = null
) : Parcelable