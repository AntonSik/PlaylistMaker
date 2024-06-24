package com.example.playlistmaker.data.dto


class SearchTrackResponse(
    val resultsCount: Int,
    val results: List<TrackDto>
): Response()
