package com.example.playlistmaker.data.repository

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.domain.models.AudioPlayerState
import com.example.playlistmaker.domain.repository.PlayerRepository

class PlayerRepositoryImpl(val context: Context) : PlayerRepository {


    private var preparedCallback: (() -> Unit)? = null
    private var audioPlayerListener: ((AudioPlayerState) -> Unit)? = null
    private var playerState = AudioPlayerState.DEFAULT
    private val mediaPlayer = MediaPlayer()


    override fun startPlayer(callback: () -> Unit) {
        mediaPlayer.start()
        playerState = AudioPlayerState.PLAYING
    }

    override fun pausePlayer(callback: () -> Unit) {
        mediaPlayer.pause()
        playerState = AudioPlayerState.PAUSED
    }

    override fun preparePlayer(recordsUrl: String?) {
        mediaPlayer.setDataSource(recordsUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            preparedCallback?.invoke()
        }
    }


    override fun setOnPreparedCallback(callback: () -> Unit) {
        preparedCallback = callback
    }

    override fun setOnCompletionCallBack(callback: () -> Unit) {
        playerState = AudioPlayerState.PREPARED
    }

    override fun setOnChangePlayerListener(listener: (AudioPlayerState) -> Unit) {
        audioPlayerListener = listener
    }
}