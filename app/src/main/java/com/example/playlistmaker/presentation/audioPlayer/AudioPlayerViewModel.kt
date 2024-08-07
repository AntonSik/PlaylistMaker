package com.example.playlistmaker.presentation.audioPlayer


import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audioPlayer.models.PlayerScreenState
import com.example.playlistmaker.ui.audioPlayer.models.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val track: Track?,
    private val playerInteractor: PlayerInteractor,
    private var mediaPlayer: MediaPlayer
) : ViewModel() {
    companion object {
        private const val UPDATING_DELAY = 300L
    }

    private val recordsUrl = track?.previewUrl

    private var timerJob: Job? = null


    private val screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    val screenStateLive: LiveData<PlayerScreenState> = screenStateLiveData

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    init {
        playerInteractor.loadTrackData(
            track = track
        ) { playerModel ->
            screenStateLiveData.postValue(
                PlayerScreenState.Content(playerModel)
            )
        }
        preparePlayer()
    }

    private fun play() {
        mediaPlayer.start()
        playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pause() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerState.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
    }


    private fun release() {
        mediaPlayer.stop()
        mediaPlayer.release()
        playerState.value = PlayerState.Default()
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }

    fun onPause() {
        pause()
    }

    fun playBackControl() {
        when (playerState.value) {
            is PlayerState.Playing -> {
                pause()
            }

            is PlayerState.Prepared, is PlayerState.Paused -> {
                play()
            }

            else -> preparePlayer()
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(UPDATING_DELAY)
                playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.currentPosition) ?: "00:00"
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(recordsUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState.postValue(PlayerState.Prepared())
        }
        mediaPlayer.setOnCompletionListener {
            playerState.postValue(PlayerState.Prepared())
        }
    }

}