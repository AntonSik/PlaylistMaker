package com.example.playlistmaker.data.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.storage.db.dao.TrackDao
import com.example.playlistmaker.data.storage.db.entity.TrackEntity

@Database(version = 3, entities = [TrackEntity::class])
abstract class TracksDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
}