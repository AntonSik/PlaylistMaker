package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SearchTracksRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
    fun addTrack(track: Track)
    fun getHistoryList(): ArrayList<Track>
    fun clearHistory()
}