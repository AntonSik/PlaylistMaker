package com.example.playlistmaker.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SearchTrackInteracktor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.models.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchTracksViewModel(

    private val searchInteractor: SearchTrackInteracktor,

    ) : ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var lastSearchText: String? = null
    private var searchJob: Job? = null


    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val trackListMutable = MutableLiveData<List<Track>>()
    val trackListLiveData: LiveData<List<Track>> = trackListMutable

    fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) {
            return
        }
        lastSearchText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            viewModelScope.launch {
                searchInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val trackList = mutableListOf<Track>()
        if (foundTracks != null) {
            trackList.addAll(foundTracks)
        }
        when {
            errorMessage != null -> {
                renderState(
                    SearchState.Error(
                        errorMessage = R.string.no_internet_connection,
                        errorMessageExtra = R.string.no_internet_connection_extra
                    )
                )
            }

            trackList.isEmpty() -> {
                renderState(
                    SearchState.Empty(
                        message = R.string.not_found
                    )
                )
            }

            else -> {
                renderState(
                    SearchState.Content(
                        tracks = trackList,
                        isHistory = false
                    )
                )
                trackListMutable.postValue(trackList)
            }

        }
    }

    fun resetLastSearchedText() {
        lastSearchText = null
    }

    fun addToHistory(track: Track) {
        searchInteractor.addTrack(track)
    }

    fun getHistory(): ArrayList<Track> {
        val history = ArrayList<Track>()
        viewModelScope.launch {
            searchInteractor.getHistoryList().collect { historyList ->
                renderState(
                    SearchState.History(
                        history = ArrayList(historyList),
                        isHistory = true
                    )
                )
            }
        }
        return history
    }


    fun clearHistory() {
        searchInteractor.clearHistory()
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)

    }

    private fun getHistoryState() {
        getHistory()

    }

    private fun getContentState() {
        renderState(
            SearchState.Content(
                tracks = trackListLiveData.value ?: emptyList(),
                isHistory = false
            )
        )
    }

    fun setHistoryFlag(isHistory: Boolean) {
        if (isHistory) getHistoryState() else getContentState()

    }


}