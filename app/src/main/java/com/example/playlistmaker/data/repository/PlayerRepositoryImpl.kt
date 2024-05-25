package com.example.playlistmaker.data.repository

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.playlistmaker.domain.models.AudioPlayerState
import com.example.playlistmaker.domain.repository.PlayerRepository

class PlayerRepositoryImpl(val context: Context) : PlayerRepository {


    //private var preparedCallback: (() -> Unit)? = null
    private var audioPlayerListener: ((AudioPlayerState) -> Unit)? = null
    private val mediaPlayer = MediaPlayer()


    override fun startPlayer(callback: () -> Unit) {
        mediaPlayer.start()
        audioPlayerListener?.invoke(AudioPlayerState.PLAYING)
        Log.d("IMPL"," player is playing")
    }

    override fun pausePlayer(callback: () -> Unit) {
        mediaPlayer.pause()
        audioPlayerListener?.invoke(AudioPlayerState.PAUSED)
        Log.d("IMPL"," player is paused")
    }

    override fun preparePlayer(recordsUrl: String?) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(recordsUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            audioPlayerListener?.invoke(AudioPlayerState.PREPARED)


        }
        mediaPlayer.setOnCompletionListener {
            audioPlayerListener?.invoke(AudioPlayerState.COMPLETED)

        }

    }

    override fun getDefault() {
        audioPlayerListener?.invoke(AudioPlayerState.DEFAULT)
    }

    override fun getCurrentPosition() : Int{
        return mediaPlayer.currentPosition
    }

    override fun releasePlayer(callback: () -> Unit) {
        mediaPlayer.release()
        audioPlayerListener?.invoke(AudioPlayerState.DELETED)
    }

    override fun setOnChangePlayerListener(listener: (AudioPlayerState) -> Unit) {
        audioPlayerListener = listener
    }
}