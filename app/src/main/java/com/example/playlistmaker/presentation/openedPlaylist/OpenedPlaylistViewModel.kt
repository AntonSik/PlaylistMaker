package com.example.playlistmaker.presentation.openedPlaylist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.openedPlaylist.models.PlaylistScreenState
import com.example.playlistmaker.ui.openedPlaylist.models.PlaylistTracksState
import kotlinx.coroutines.launch

class OpenedPlaylistViewModel(
    private val context: Context,
    private val interactor: PlaylistInteractor,
    private val playlistId: Int
) : ViewModel() {

    private val screenStateLiveData =
        MutableLiveData<PlaylistScreenState>(PlaylistScreenState.Loading)
    val screenStateLive: LiveData<PlaylistScreenState> = screenStateLiveData

    private val playlistTracksLiveData = MutableLiveData<PlaylistTracksState>()
    val playlistTracksLive: LiveData<PlaylistTracksState> = playlistTracksLiveData

    private val minutesOfAllTracksLiveData = MutableLiveData<Long>()
    val minutesOfAllTracksLive: LiveData<Long> = minutesOfAllTracksLiveData

    private val countOfAllTracksLiveData = MutableLiveData<Int>()
    val countOfAllTracksLive: LiveData<Int> = countOfAllTracksLiveData

    init {

        loadPlaylistData()
    }

    private fun loadTracks() {

        viewModelScope.launch {

            render(PlaylistTracksState.Loading)
            interactor.getTracksOfPlaylist(playlistId)
                .collect { trackList ->
                    showTracks(trackList)
                }
        }
    }

    fun loadPlaylistData() {
        viewModelScope.launch {

            interactor.loadPlaylistDataById(playlistId) { playlistModel ->
                screenStateLiveData.postValue(PlaylistScreenState.Content(playlistModel))
                loadTracks()
            }
        }
    }

    fun deleteTrack(trackId: Int) {

        viewModelScope.launch {

            interactor.deleteTrackFromPlaylist(trackId, playlistId)
            loadTracks()

        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {

            interactor.deletePlaylist(playlistId)
        }
    }

    private fun showTracks(tracks: List<Track>) {

        if (tracks.isEmpty()) {
            render(PlaylistTracksState.Empty(context.getString(R.string.empty_playlist_tracks_placeholder_message)))
        } else {
            render(PlaylistTracksState.Content(tracks))
        }
        val countSum = tracks.size

        val durationSum = tracks.sumOf { track ->
            track.trackTimeMillis
        }
        minutesOfAllTracksLiveData.postValue(durationSum)
        countOfAllTracksLiveData.postValue(countSum)
    }

    private fun render(state: PlaylistTracksState) {
        playlistTracksLiveData.postValue(state)
    }

    fun sharePlaylistText(): String {
        val playlistModel =
            (screenStateLiveData.value as? PlaylistScreenState.Content)?.playlistModel
        val tracks =
            (playlistTracksLiveData.value as? PlaylistTracksState.Content)?.trackList ?: emptyList()

        val trackListText = tracks.mapIndexed { index, track ->
            "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeMillis / 1000 / 60}:${
                (track.trackTimeMillis / 1000 % 60).toString().padStart(2, '0')
            })"
        }.joinToString("\n")
        return """
            |${playlistModel?.title}
            |${playlistModel?.description}
            |${changingTracksCountEnding(tracks.size)} 
            |
            |$trackListText
        """.trimMargin()
    }

    private fun changingTracksCountEnding(tracksCount: Int?): String {
        val trackEnding = context.resources
        val defaultEnding = context.getString(R.string.zero_tracks)
        return if (tracksCount == null) {
            defaultEnding
        } else {
            return trackEnding.getQuantityString(
                R.plurals.count_of_tracks,
                tracksCount,
                tracksCount
            )
        }
    }

}