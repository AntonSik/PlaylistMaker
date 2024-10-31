package com.example.playlistmaker.data.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.storage.db.dao.PlaylistTrackDao
import com.example.playlistmaker.data.storage.db.dao.PlaylistsDao
import com.example.playlistmaker.data.storage.db.dao.TrackDao
import com.example.playlistmaker.data.storage.db.entity.PlayListTrackEntity
import com.example.playlistmaker.data.storage.db.entity.PlaylistEntity
import com.example.playlistmaker.data.storage.db.entity.TrackEntity

@Database(
    version = 6,
    entities = [TrackEntity::class, PlaylistEntity::class, PlayListTrackEntity::class]
)
abstract class TracksDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistsDao

    abstract fun playlistTrackDao(): PlaylistTrackDao

}