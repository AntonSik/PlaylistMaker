package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.storage.db.dao.PlaylistTrackDao
import com.example.playlistmaker.data.storage.db.dao.PlaylistsDao
import com.example.playlistmaker.data.storage.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.PlaylistsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistsDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistConverter: PlaylistDbConverter,
    private val trackConverter: TrackDbConverter,
    private val gson: Gson
) : PlaylistsRepository {
    override suspend fun addNewPlaylist(playlist: Playlist) {
        val playlistEntity = playlistConverter.mapPlaylistToEntity(playlist)
        playlistDao.insertPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = playlistConverter.mapPlaylistToEntity(playlist)
        playlistDao.deletePlaylist(playlistEntity)
    }

    override suspend fun update(playlist: Playlist) {
        val playlistEntity = playlistConverter.mapPlaylistToEntity(playlist)
        playlistDao.update(playlistEntity)
    }


    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {

        val trackIdsList: MutableList<Int> = gson.fromJson(playlist.trackIds, object :
            TypeToken<List<Int>>() {}.type)
        trackIdsList.add(track.trackId)
        val updatedTrackIds = gson.toJson(trackIdsList)
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            trackCount = trackIdsList.size
        )
        playlistDao.update(playlistConverter.mapPlaylistToEntity(updatedPlaylist))
        playlistTrackDao.insertTrack(trackConverter.mapTrackToPlaylistTrackEntity(track))
    }

    override suspend fun checkTrackInPlaylist(trackId: Int, playlist: Playlist): Boolean {
        val trackIdsList: List<Int> = gson.fromJson(playlist.trackIds, object :
            TypeToken<List<Int>>() {}.type)
        return trackIdsList.contains(trackId)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists().map { convertFromPlaylistEntity(it) }
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistConverter.mapEntityToPlaylist(playlist) }
    }

    private suspend fun updateTrackIds(playlistId: Int, trackIds: String, trackCount: Int) {
        playlistDao.updateTrackIds(playlistId, trackIds, trackCount)
    }

}