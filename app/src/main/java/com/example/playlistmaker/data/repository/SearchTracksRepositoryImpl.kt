package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.SearchTrackRequest
import com.example.playlistmaker.data.dto.SearchTrackResponse
import com.example.playlistmaker.data.storage.LocalStorage
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.SearchTracksRepository
import com.example.playlistmaker.utils.Resource

class SearchTracksRepositoryImpl(
    private val networkClient: NetworkClient,
    val localStorage: LocalStorage
) : SearchTracksRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(SearchTrackRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            200 -> {

                Resource.Success((response as SearchTrackResponse).results.map {
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
                    )
                })
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }
   override fun addTrack(track: Track) {
       localStorage.addTrack(track)
    }

    override fun getHistoryList(): ArrayList<Track> {
       return localStorage.getHistoryList()
    }

    override fun clearHistory() {
        localStorage.clearHistory()
    }

}