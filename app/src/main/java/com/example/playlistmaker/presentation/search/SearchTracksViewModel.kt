package com.example.playlistmaker.presentation.search

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SearchTrackInteracktor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.models.SearchState

class SearchTracksViewModel(

    private val searchInteractor: SearchTrackInteracktor
) : ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val handler = Handler(Looper.getMainLooper())
    private var lastSearchText: String? = null


    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val trackListMutable = MutableLiveData<List<Track>>()
    val trackListLiveData: LiveData<List<Track>> = trackListMutable


    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) {
            return
        }
        this.lastSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)


            searchInteractor.searchTracks(
                newSearchText,
                object : SearchTrackInteracktor.TracksConsumer {

                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
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

                })
        }
    }

    fun resetLastSearchedText() {
        lastSearchText = null
    }

    fun addToHistory(track: Track) {
        searchInteractor.addTrack(track)
    }

    fun getHistory(): ArrayList<Track> {
        return searchInteractor.getHistoryList()
    }

    fun clearHistory() {
        searchInteractor.clearHistory()
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)

    }

    private fun getHistoryState() {
        renderState(
            SearchState.History(
                history = getHistory(),
                isHistory = true
            )
        )

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