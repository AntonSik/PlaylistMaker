package com.example.playlistmaker.data.repository

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.repository.PlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(private val context: Context) : PlayerRepository {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val UPDATING_DELAY = 400L
    }

    private var playerState = STATE_DEFAULT
    val handler = Handler(Looper.getMainLooper())
    val mediaPlayer = MediaPlayer()

    override fun startPlayer(playBtn: ImageButton) {
        mediaPlayer.start()
        playBtn.setImageResource(R.drawable.pause_vector)
        playerState = STATE_PLAYING
    }

    override fun pausePlayer(playBtn: ImageButton) {
        mediaPlayer.pause()
        playBtn.setImageResource(R.drawable.play_vector)
        playerState = STATE_PAUSED
    }

    override fun preparePlayer(playBtn: ImageButton, timePlaying: TextView, recordsUrl: String?) {
        mediaPlayer.setDataSource(recordsUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playBtn.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            timePlaying.text = context.getString(R.string.zero)
            playBtn.setImageResource(R.drawable.play_vector)
            handler.removeCallbacksAndMessages(null)
        }
    }

    override fun playBackControl(playBtn: ImageButton, timePlaying: TextView) {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer(playBtn)
                handler.removeCallbacksAndMessages(null)
            }

            STATE_PREPARED, STATE_PAUSED -> {

                startPlayer(playBtn)
                val startPlaying = System.currentTimeMillis()   //время начала отсчета
                handler.post(createPlayingTimer(startPlaying, timePlaying))
            }
        }
    }

    override fun createPlayingTimer(startTime: Long, timePlaying: TextView): Runnable {
        return object : Runnable {
            override fun run() {

                val countedTime = startTime + mediaPlayer.currentPosition

                if (countedTime > 0) {
                    if (playerState == STATE_PLAYING) {
                        timePlaying.text = SimpleDateFormat(
                            "mm:ss",
                            Locale.getDefault()
                        ).format(mediaPlayer.currentPosition)
                        handler.postDelayed(this, UPDATING_DELAY)
                    }
                }

            }
        }
    }

}