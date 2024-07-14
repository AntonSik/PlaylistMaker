package com.example.playlistmaker.presentation.audioPlayer

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.models.AudioPlayerState
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audioPlayer.models.PlayerScreenState
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    application: Application,
    private val track: Track?,
    private val playerInteractor: PlayerInteractor
) : AndroidViewModel(application) {
    companion object {
        private const val UPDATING_DELAY = 400L
    }

    private val recordsUrl = track?.previewUrl
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val handler = Handler(Looper.getMainLooper())

    private val playStateLiveData = MutableLiveData(
        PlayerStatus(
            isPlaying = false,
            isCompleted = false,
            state = AudioPlayerState.DEFAULT
        )
    )
    val statusLive: LiveData<PlayerStatus> = playStateLiveData

    private val timerLiveData = MutableLiveData<String>()
    val timerLive: LiveData<String> = timerLiveData

    private val screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    val screenStateLive: LiveData<PlayerScreenState> = screenStateLiveData

    init {
        playerInteractor.loadTrackData(
            track = track
        ) { playerModel ->
            screenStateLiveData.postValue(
                PlayerScreenState.Content(playerModel)
            )
        }
        prepare()
    }

    private fun play() {
        playerInteractor.startPlayer()
        playStateLiveData.value = playStateLiveData.value?.copy(
            isPlaying = true,
            isCompleted = false,
            state = AudioPlayerState.PLAYING
        )
    }

    fun pause() {
        playerInteractor.pausePlayer()
        playStateLiveData.value = playStateLiveData.value?.copy(
            isPlaying = false,
            isCompleted = false,
            state = AudioPlayerState.PAUSED
        )
    }

    private fun prepare() {
        playerInteractor.preparePlayer(track?.previewUrl)
        playStateLiveData.value =
            playStateLiveData.value?.copy(isPlaying = false, state = AudioPlayerState.PREPARED)
        playerInteractor.setOnCompletionCallback {
            playStateLiveData.value = playStateLiveData.value?.copy(
                isPlaying = false,
                isCompleted = true,
                state = AudioPlayerState.PREPARED
            )
        }
    }

    fun release() {
        playerInteractor.releasePlayer()
        playStateLiveData.value = playStateLiveData.value?.copy(state = AudioPlayerState.DELETED)
    }

    override fun onCleared() {
        release()
    }

    fun playBackControl() {
        when (statusLive.value?.state) {
            AudioPlayerState.PLAYING -> {

                pause()
                handler.removeCallbacksAndMessages(null)
            }

            AudioPlayerState.PREPARED, AudioPlayerState.PAUSED -> {

                play()
                val startPlaying = System.currentTimeMillis()
                handler.post(createPlayingTimer(startPlaying))
            }

            AudioPlayerState.DELETED -> {
                playerInteractor.getDefault()
                handler.removeCallbacksAndMessages(null)
            }

            else -> playerInteractor.preparePlayer(recordsUrl)
        }
    }

    private fun createPlayingTimer(startTime: Long): Runnable {
        return object : Runnable {
            override fun run() {

                val countedTime = startTime + playerInteractor.getCurrentPosition()

                if (countedTime > 0) {
                    if (playStateLiveData.value?.state == AudioPlayerState.PLAYING) {
                        timerLiveData.value =
                            dateFormat.format(playerInteractor.getCurrentPosition()).toString()
                        handler.postDelayed(this, UPDATING_DELAY)
                    }
                }
            }
        }
    }

    data class PlayerStatus(
        val isPlaying: Boolean,
        val isCompleted: Boolean,
        val state: AudioPlayerState
    )
}