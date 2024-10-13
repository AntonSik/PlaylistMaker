package com.example.playlistmaker.domain.models

data class Playlist(
    val playlistId: Int = 0,
    val title: String,
    val description: String?,
    val filePath: String?,
    val trackIds: String?,
    val trackCount: Int?
)