package com.example.playlistmaker.data.repository


import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.SearchTrackRequest
import com.example.playlistmaker.data.dto.SearchTrackResponse
import com.example.playlistmaker.data.storage.LocalStorage
import com.example.playlistmaker.data.storage.db.TracksDatabase
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.SearchTracksRepository
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchTracksRepositoryImpl(
    private val context: Context,
    private val networkClient: NetworkClient,
    private val localStorage: LocalStorage,
    private val tracksDatabase: TracksDatabase,
) : SearchTracksRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(SearchTrackRequest(expression))
        when(response.resultCode){
            -1 -> {
               emit(Resource.Error(context.getString(R.string.check_internet_connection)))
            }

            200 -> {
                with(response as SearchTrackResponse){
                val favoriteTracksId = getFavoritesId()
                val data = results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.primaryGenreName,
                        it.releaseDate,
                        it.country,
                        it.previewUrl,
                        isFavorite = favoriteTracksId.contains(it.trackId)
                    )
                }
                    emit(Resource.Success(data))
                }
            }
            else -> {
               emit(Resource.Error(context.getString(R.string.server_error)))
            }
        }
    }

    override fun addTrack(track: Track) {
        localStorage.addTrack(track)
    }

    override fun getHistoryList(): Flow<List<Track>> = flow {
        val historyList =  localStorage.getHistoryList()
        val favoritesHistoryId = getFavoritesId()

        val data = historyList.map { track ->
            track.isFavorite = favoritesHistoryId.contains(track.trackId)
            track
        }
       emit(data)
    }

    override fun clearHistory() {
        localStorage.clearHistory()
    }

    private suspend fun getFavoritesId(): List<Int>{
       return tracksDatabase.trackDao().getFavoritesTracksId()
    }

}