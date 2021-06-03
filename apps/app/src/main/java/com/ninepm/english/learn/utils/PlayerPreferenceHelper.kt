package com.ninepm.english.learn.utils

import android.content.Context
import android.util.Log
import com.ninepm.english.learn.data.source.local.entity.PlayerEntity

class PlayerPreferenceHelper(context: Context) {
    companion object {
        const val PREFS_NAME = "MEDIA_RECORDER_STATE"
        private const val RECORDER_STATE = "state"
        private const val PLAYER_STATE = "state"
        private const val PLAYER_MEDIA_STATE = "state"
    }
    private val preference = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setState(player: PlayerEntity) {
        val state = preference.edit()
        state.putString(PLAYER_STATE, player.playerState)
        state.putString(PLAYER_MEDIA_STATE, player.playerMediaState)
        state.putString(RECORDER_STATE, player.recorderState)
        state.apply()
    }
    fun getState(): PlayerEntity {
        val player = PlayerEntity()
        player.playerState = preference.getString(PLAYER_STATE, null)
        player.playerMediaState = preference.getString(PLAYER_MEDIA_STATE, null)
        player.recorderState = preference.getString(RECORDER_STATE, null)
        return player
    }
}