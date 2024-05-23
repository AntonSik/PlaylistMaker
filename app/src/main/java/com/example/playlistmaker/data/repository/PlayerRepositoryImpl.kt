package com.example.playlistmaker.data.repository

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.domain.models.AudioPlayerState
import com.example.playlistmaker.domain.repository.PlayerRepository

class PlayerRepositoryImpl(val context: Context) : PlayerRepository {


    //private var preparedCallback: (() -> Unit)? = null
    private var audioPlayerListener: ((AudioPlayerState) -> Unit)? = null
    var playerState = AudioPlayerState.DEFAULT
    private val mediaPlayer = MediaPlayer()


    override fun startPlayer(callback: () -> Unit) {
        mediaPlayer.start()
        audioPlayerListener?.invoke(AudioPlayerState.PLAYING)
        playerState = AudioPlayerState.PLAYING
    }

    override fun pausePlayer(callback: () -> Unit) {
        mediaPlayer.pause()
        audioPlayerListener?.invoke(AudioPlayerState.PAUSED)
        playerState = AudioPlayerState.PAUSED
    }

    override fun preparePlayer(recordsUrl: String?) {
        mediaPlayer.setDataSource(recordsUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            audioPlayerListener?.invoke(AudioPlayerState.PREPARED)
            playerState = AudioPlayerState.PREPARED
        }
    }

    override fun getCurrentPosition() : Int{
        return mediaPlayer.currentPosition
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun setOnChangePlayerListener(listener: (AudioPlayerState) -> Unit) {
        audioPlayerListener = listener
    }
}