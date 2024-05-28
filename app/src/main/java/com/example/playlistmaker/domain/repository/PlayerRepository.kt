package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.AudioPlayerState

interface PlayerRepository {

    fun startPlayer()

    fun pausePlayer()

    fun getDefault()

    fun preparePlayer(recordsUrl: String?)
    fun setOnCompletionCallback(callback: () -> Unit)

    fun getCurrentPosition():Int
    fun releasePlayer()

    fun setOnChangePlayerListener(listener: (AudioPlayerState) -> Unit)
}