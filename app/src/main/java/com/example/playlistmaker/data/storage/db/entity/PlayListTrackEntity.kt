package com.example.playlistmaker.data.storage.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "playlist_tracks_table")
data class PlayListTrackEntity(
    @PrimaryKey
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val primaryGenreName: String,
    val releaseDate: String,
    val country: String,
    val previewUrl: String,
    val timeStamp: Long = System.currentTimeMillis() / 1000
)
