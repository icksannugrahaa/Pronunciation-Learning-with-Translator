package com.ninepm.english.learn.data.source.local.entity

data class PlayerEntity(
    var playerState: String? = "stop",
    var playerMediaState: String? = "not_available",
    var recorderState: String? = "false"
)