package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchTrackInteracktor {

    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>
    fun addTrack(track: Track)
    fun getHistoryList(): Flow<List<Track>>

    fun clearHistory()


}