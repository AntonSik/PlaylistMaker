package com.example.playlistmaker

import android.content.SharedPreferences


data class Track(
    val trackId : Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
)
