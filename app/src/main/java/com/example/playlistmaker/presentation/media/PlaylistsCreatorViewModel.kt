package com.example.playlistmaker.presentation.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistsCreatorViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val playlistsCoverPathlist = mutableListOf<String>()

    fun addNewPlaylistToDb(playlist: Playlist) {
        viewModelScope.launch {
            interactor.addNewPlaylist(playlist)
        }
    }

    fun writeFileName(filePath: String) {
        if (!playlistsCoverPathlist.contains(filePath)) {
            playlistsCoverPathlist.add(filePath)
        }
    }

    fun getImagePath(): String? {
        return playlistsCoverPathlist.lastOrNull()
    }
}