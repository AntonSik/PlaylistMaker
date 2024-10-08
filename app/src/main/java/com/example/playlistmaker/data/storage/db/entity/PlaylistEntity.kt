package com.example.playlistmaker.data.storage.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int = 0,
    val title: String,
    val description: String?,
    val filePath: String?,
    val trackIds: String = Gson().toJson(emptyList<Int>()),
    val trackCount: Int = 0
)
