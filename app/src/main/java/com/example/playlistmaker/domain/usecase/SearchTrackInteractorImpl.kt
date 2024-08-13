package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.SearchTrackInteracktor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.SearchTracksRepository
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchTrackInteractorImpl(private val repository: SearchTracksRepository) :
    SearchTrackInteracktor {
    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }


    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun getHistoryList(): ArrayList<Track> {
        return repository.getHistoryList()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}