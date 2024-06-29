package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.SearchTrackInteracktor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.SearchTracksRepository
import com.example.playlistmaker.utils.Resource
import java.util.concurrent.Executors

class SearchTrackInteractorImpl(private val repository: SearchTracksRepository) : SearchTrackInteracktor {
    private val executor = Executors.newCachedThreadPool()


    override fun searchTracks(expression: String, consumer: SearchTrackInteracktor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
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