package com.example.playlistmaker

import com.example.playlistmaker.domain.models.Track

class ItunesTrackResponse(
    val resultsCount: Int,
    val results: List<Track>
)
