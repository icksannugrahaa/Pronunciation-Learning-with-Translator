package com.ninepm.english.learn.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ResponsePredict(
	@field:SerializedName("word")
	val word: String? = null
)
