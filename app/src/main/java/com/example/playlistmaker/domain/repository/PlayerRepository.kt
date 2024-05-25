package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.AudioPlayerState

interface PlayerRepository {

    fun startPlayer(callback: () -> Unit)

    fun pausePlayer(callback: () -> Unit)

    fun getDefault()

    fun preparePlayer(recordsUrl: String?)

    fun getCurrentPosition():Int
    fun releasePlayer(callback: () -> Unit)

    fun setOnChangePlayerListener(listener: (AudioPlayerState) -> Unit)
}