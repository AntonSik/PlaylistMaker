package com.example.playlistmaker.domain.repository

interface PlayerRepository {
    fun preparePlayer(recordsUrl: String?)
    fun releasePlayer()

    fun startPlayer()

    fun pausePlayer()

    fun getDefault()
    fun getCurrentPosition(): Int

    fun setOnCompletionCallback(callback: () -> Unit)


}