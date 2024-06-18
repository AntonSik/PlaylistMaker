package com.example.playlistmaker.domain.models

data class EmailData(
    val recipient : String,
    val subject: String,
    val text: String
)
