package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchTrackInteracktor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
    fun addTrack(track: Track)
    fun getHistoryList(): ArrayList<Track>
    fun clearHistory()

    interface TracksConsumer{
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}