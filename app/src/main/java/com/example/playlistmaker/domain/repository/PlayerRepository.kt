package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.AudioPlayerState

interface PlayerRepository {

    fun startPlayer(callback: () -> Unit)

    fun pausePlayer(callback: () -> Unit)

    fun getDefault(callback: () -> Unit)

    fun preparePlayer(recordsUrl: String?)
    fun setOnCompletionCallback(callback: () -> Unit)

    fun getCurrentPosition():Int
    fun releasePlayer(callback: () -> Unit)

    fun setOnChangePlayerListener(listener: (AudioPlayerState) -> Unit)
}