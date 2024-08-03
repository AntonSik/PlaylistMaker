package com.example.playlistmaker.presentation.audioPlayer


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.models.AudioPlayerState
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audioPlayer.models.PlayerScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val track: Track?,
    private val playerInteractor: PlayerInteractor
) : ViewModel() {
    companion object {
        private const val UPDATING_DELAY = 300L
    }

    private val recordsUrl = track?.previewUrl

    private var timerJob: Job? = null

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
        timerLiveData.postValue(getCurrentPlayerPosition())
        startTimer()
    }

    fun pause() {
        playerInteractor.pausePlayer()
        playStateLiveData.value = playStateLiveData.value?.copy(
            isPlaying = false,
            isCompleted = false,
            state = AudioPlayerState.PAUSED
        )
        timerJob?.cancel()
        timerLiveData.postValue(getCurrentPlayerPosition())
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
            timerLiveData.postValue("00:00")
            timerJob?.cancel()
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
            }

            AudioPlayerState.PREPARED, AudioPlayerState.PAUSED -> {
                play()
            }

            AudioPlayerState.DELETED -> {
                playerInteractor.getDefault()
            }

            else -> playerInteractor.preparePlayer(recordsUrl)
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (statusLive.value?.isPlaying == true) {
                delay(UPDATING_DELAY)
                timerLiveData.postValue(getCurrentPlayerPosition())
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(playerInteractor.getCurrentPosition()) ?: "00:00"
    }


    data class PlayerStatus(
        val isPlaying: Boolean,
        val isCompleted: Boolean,
        val state: AudioPlayerState
    )
}