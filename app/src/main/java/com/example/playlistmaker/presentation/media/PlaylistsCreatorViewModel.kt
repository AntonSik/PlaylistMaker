package com.example.playlistmaker.presentation.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.PlaylistModel
import kotlinx.coroutines.launch

open class PlaylistsCreatorViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val playlistsCoverPathList = mutableListOf<String>()

    private val playlistLiveData = MutableLiveData<PlaylistModel>()
    val playlistLive: LiveData<PlaylistModel> = playlistLiveData

    fun addNewPlaylistToDb(playlist: Playlist) {
        viewModelScope.launch {
            interactor.addNewPlaylist(playlist)
        }
    }

    fun writeFileName(filePath: String) {
        if (!playlistsCoverPathList.contains(filePath)) {
            playlistsCoverPathList.add(filePath)
        }
    }

    fun getImagePath(): String? {
        return playlistsCoverPathList.lastOrNull()
    }

    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            interactor.loadPlaylistDataById(playlistId) { playlistModel ->
                playlistLiveData.postValue(playlistModel)
            }
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            interactor.updatePlaylist(playlist)
        }
    }
}