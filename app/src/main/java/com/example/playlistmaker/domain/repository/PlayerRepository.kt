package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.AudioPlayerState

interface PlayerRepository {

    fun startPlayer(callback: () -> Unit)

    fun pausePlayer(callback: () -> Unit)

    fun preparePlayer(recordsUrl: String?)
//    fun setOnPreparedCallback(callback: () -> Unit)
//    fun setOnCompletionCallBack(callback: () -> Unit)
    fun getCurrentPosition():Int
    fun releasePlayer()

    fun setOnChangePlayerListener(listener: (AudioPlayerState) -> Unit)
}