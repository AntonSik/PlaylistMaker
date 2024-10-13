package com.example.playlistmaker.data.storage.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS playlist_tracks_table(
                trackId INTEGER PRIMARY KEY NOT NULL,
                trackName TEXT NOT NULL,
                artistName TEXT NOT NULL,
                trackTimeMillis INTEGER NOT NULL,
                artworkUrl100 TEXT NOT NULL,
                collectionName TEXT NOT NULL,
                primaryGenreName TEXT NOT NULL,
                releaseDate TEXT NOT NULL,
                country TEXT NOT NULL,
                previewUrl TEXT NOT NULL,
                timeStamp INTEGER NOT NULL
            )
        """.trimIndent()
        )
    }

}