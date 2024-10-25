package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.storage.db.dao.PlaylistTrackDao
import com.example.playlistmaker.data.storage.db.dao.PlaylistsDao
import com.example.playlistmaker.data.storage.db.entity.PlayListTrackEntity
import com.example.playlistmaker.data.storage.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.PlaylistsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

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

    override suspend fun deletePlaylist(playlistId: Int) {

        withContext(Dispatchers.IO) {
            val playlistTracks = getPlaylistById(playlistId).trackIds
            playlistDao.deletePlaylist(playlistId)
            val trackIdsList: List<Int> = gson.fromJson(playlistTracks, object :
                TypeToken<List<Int>>() {}.type)

            trackIdsList.forEach { trackId ->
                checkRelationsAndDeleteTrackFromTable(trackId)
            }
        }
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

    override suspend fun getPlaylistById(playlistId: Int): Playlist {

        val playlistEntity = playlistDao.getPlaylistById(playlistId)
        return playlistConverter.mapEntityToPlaylist(playlistEntity)
    }

    override suspend fun getTracksByIds(playlistId: Int): List<PlayListTrackEntity> {

        val playlist = getPlaylistById(playlistId).trackIds
        val trackIdsList: List<Int> = gson.fromJson(playlist, object :
            TypeToken<List<Int>>() {}.type)

        return playlistTrackDao.getTracksByIds(trackIdsList)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int) {
        withContext(Dispatchers.IO) {
            val playlist = getPlaylistById(playlistId)
            val trackIdsList: MutableList<Int> = gson.fromJson(playlist.trackIds, object :
                TypeToken<MutableList<Int>>() {}.type)
            trackIdsList.remove(trackId)
            val newSize = trackIdsList.size
            val updatedListOfTracks = gson.toJson(trackIdsList)

            val updatedPlaylist = playlist.copy(
                trackIds = updatedListOfTracks,
                trackCount = newSize
            )
            playlistDao.update(playlistConverter.mapPlaylistToEntity(updatedPlaylist))
            checkRelationsAndDeleteTrackFromTable(trackId)
        }
    }

    override suspend fun checkRelationsAndDeleteTrackFromTable(trackId: Int) {
        withContext(Dispatchers.IO) {
            val allPlaylists = getAllPlaylists().first()
            var trackFound = false

            allPlaylists.forEach { playlist ->
                val trackIdsList: List<Int> =
                    gson.fromJson(playlist.trackIds, object : TypeToken<List<Int>>() {}.type)
                if (trackIdsList.contains(trackId)) {
                    trackFound = true
                    return@forEach
                }
            }
            if (!trackFound) {
                playlistTrackDao.deleteTrackFromTable(trackId)
            }
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {

        return playlistDao.getPlaylists().map { convertFromPlaylistEntity(it) }
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {

        return playlists.map { playlist -> playlistConverter.mapEntityToPlaylist(playlist) }
    }

}