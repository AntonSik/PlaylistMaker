package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audioPlayer.models.PlayerModel

interface PlayerInteractor {

    fun loadTrackData(track: Track?, onComplete: (PlayerModel) -> Unit)

    fun preparePlayer(recordsUrl: String?)
    fun releasePlayer()

    fun startPlayer()

    fun pausePlayer()

    fun getDefault()
    fun getCurrentPosition(): Int

    fun setOnCompletionCallback(callback: () -> Unit)

}