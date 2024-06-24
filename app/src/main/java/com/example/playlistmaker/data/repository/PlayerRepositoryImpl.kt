package com.example.playlistmaker.data.repository


import android.media.MediaPlayer
import com.example.playlistmaker.domain.models.AudioPlayerState
import com.example.playlistmaker.domain.repository.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {

    private var completionCallback: (() -> Unit)? = null
    private var audioPlayerListener: ((AudioPlayerState) -> Unit)? = null
    private val mediaPlayer = MediaPlayer()


    override fun startPlayer() {
        mediaPlayer.start()
        audioPlayerListener?.invoke(AudioPlayerState.PLAYING)
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        audioPlayerListener?.invoke(AudioPlayerState.PAUSED)
    }

    override fun preparePlayer(recordsUrl: String?) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(recordsUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            audioPlayerListener?.invoke(AudioPlayerState.PREPARED)

        }
        mediaPlayer.setOnCompletionListener {
            completionCallback?.invoke()
            audioPlayerListener?.invoke(AudioPlayerState.PREPARED)
        }
    }

    override fun setOnCompletionCallback(callback: () -> Unit) {
        completionCallback = callback
    }


    override fun getDefault() {
        audioPlayerListener?.invoke(AudioPlayerState.DEFAULT)
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun releasePlayer() {
        mediaPlayer.release()
        audioPlayerListener?.invoke(AudioPlayerState.DELETED)
    }
}