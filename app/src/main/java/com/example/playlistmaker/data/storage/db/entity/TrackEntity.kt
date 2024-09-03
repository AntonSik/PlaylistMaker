package com.example.playlistmaker.data.storage.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tracks_table")
data class TrackEntity (
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
    val timeStamp: Long = System.currentTimeMillis()/1000
)
