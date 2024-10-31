package com.example.playlistmaker.domain.models

data class PlaylistModel(
    val playlistId: Int,
    val title: String,
    val description: String?,
    val filePath: String?,
    val trackIds: String?,
    val trackCount: Int?
)
