package com.example.playlistmaker.data.dto

data class PlaylistDto(
    val playlistId: Int,
    val title: String,
    val description: String,
    val filePath: String,
    val trackIds: String,
    val trackCount: Int
)