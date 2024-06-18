package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.utils.Resource

interface SearchTracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
    fun addTrack(track: Track)
    fun getHistoryList(): ArrayList<Track>
    fun clearHistory()
}