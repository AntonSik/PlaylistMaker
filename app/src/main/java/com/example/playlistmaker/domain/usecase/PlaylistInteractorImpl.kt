package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.PlaylistModel
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.PlaylistsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistInteractor {
    override suspend fun addNewPlaylist(playlist: Playlist) {
        repository.addNewPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlistId: Int) {

        repository.deletePlaylist(playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int) {
        repository.deleteTrackFromPlaylist(trackId, playlistId)

    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.update(playlist)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun checkTrackInPlaylist(trackId: Int, playlist: Playlist): Boolean {
        return repository.checkTrackInPlaylist(trackId, playlist)
    }

    override suspend fun getTracksOfPlaylist(playlistId: Int): Flow<List<Track>> = flow {
        val trackEntities = repository.getTracksByIds(playlistId)
        val tracks = trackEntities.map { trackEntity ->
            Track(
                trackId = trackEntity.trackId,
                trackName = trackEntity.trackName,
                artistName = trackEntity.artistName,
                trackTimeMillis = trackEntity.trackTimeMillis,
                artworkUrl100 = trackEntity.artworkUrl100,
                primaryGenreName = trackEntity.primaryGenreName,
                collectionName = trackEntity.collectionName,
                releaseDate = trackEntity.releaseDate,
                country = trackEntity.country,
                previewUrl = trackEntity.previewUrl,
            )
        }
        emit(tracks)
    }.flowOn(Dispatchers.IO)

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }

    override fun loadPlaylistDataById(playlistId: Int, onComplete: (PlaylistModel) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {

            val playlist = repository.getPlaylistById(playlistId)
            val playlistModel = PlaylistModel(
                playlistId = playlist.playlistId,
                title = playlist.title,
                description = playlist.description,
                filePath = playlist.filePath,
                trackIds = playlist.trackIds,
                trackCount = playlist.trackCount
            )

            withContext(Dispatchers.Main) {
                onComplete(playlistModel)
            }
        }

    }
}