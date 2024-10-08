package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.storage.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist
import com.google.gson.Gson

class PlaylistDbConverter {

    fun mapPlaylistToEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.title,
            playlist.description,
            playlist.filePath,
            playlist.trackIds ?: Gson().toJson(emptyList<Int>()),
            playlist.trackCount ?: 0,
        )
    }

    fun mapEntityToPlaylist(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistEntity.playlistId,
            playlistEntity.title,
            playlistEntity.description,
            playlistEntity.filePath,
            playlistEntity.trackIds,
            playlistEntity.trackCount
        )
    }
}