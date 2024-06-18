package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.SearchTracksViewModel
import com.example.playlistmaker.ui.audioPlayer.AudioPlayerActivity
import com.example.playlistmaker.ui.search.models.SearchState


class SearchActivity : AppCompatActivity() {

    companion object {
        private const val INPUT_TEXT = "INPUT_EDIT"
        const val CLICKED_ITEM = "clicked track"
        private const val CLICK_DEBOUNCE_DELAY = 2000L

    }

    private lateinit var binding: ActivitySearchBinding

    private var savedText: String? = null
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var viewModel: SearchTracksViewModel

    private lateinit var trackList: ArrayList<Track>
    private lateinit var historyTracks: ArrayList<Track>
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var textWatcher: TextWatcher? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SearchTracksViewModel.getViewModelFactory()
        )[SearchTracksViewModel::class.java]

        savedText = savedInstanceState?.getString(INPUT_TEXT)
        init()

        binding.arrow2.setOnClickListener {
            finish()
        }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            trackList.clear()
            hideKeyboard()

        }
        binding.bClearHistoryBtn.setOnClickListener {
            viewModel.clearHistory()
            historyTracks.clear()
            historyAdapter.tracks = viewModel.getHistory()
            historyAdapter.notifyDataSetChanged()
            binding.bClearHistoryBtn.visibility = View.GONE
            binding.tvSearched.visibility = View.GONE
        }
        binding.bPlaceholderUpdateBtn.setOnClickListener {
            viewModel.resetLastSearchedText()
            viewModel.searchDebounce(savedText.toString())
        }

// TextWatcher

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                savedText = s.toString()
                binding.placeholder.visibility = View.GONE
                binding.clearIcon.visibility = clearButtonVisibility(s)
                viewModel.searchDebounce(changedText = s?.toString() ?: "")
                if (binding.inputEditText.hasFocus() && binding.inputEditText.text.isNotEmpty()) {
                    trackAdapter.tracks.clear()
                    binding.rvRecycleView.adapter = trackAdapter
                    binding.placeholder.visibility = View.GONE
                    binding.bClearHistoryBtn.visibility = View.GONE
                    binding.tvSearched.visibility = View.GONE

                }else if (binding.inputEditText.hasFocus() && binding.inputEditText.text.isEmpty() && viewModel.getHistory().isEmpty()) {
                    showContent(trackList)
                }else {
                    showHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcher?.let { binding.inputEditText.addTextChangedListener(it) }

        trackList = arrayListOf()
        trackAdapter = TrackAdapter(trackList, object : TrackAdapter.OnClickListenerItem {
            override fun onItemClick(track: Track) {
                if (clickDebounce()) {
                    viewModel.addToHistory(track)
                    val playerIntent = Intent(this@SearchActivity, AudioPlayerActivity::class.java)
                    playerIntent.putExtra(CLICKED_ITEM, track)
                    startActivity(playerIntent)
                }
            }

        })
        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.inputEditText.text.isEmpty() && viewModel.getHistory()
                    .isNotEmpty()
            ) {

                showHistory()                               // Кейс когда история поиска не пуста

            }else {

                showContent(trackList)                      // Кейс когда история пуста

            }
        }

        viewModel.observeState().observe(this) {
            render(it)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }

    }

    private fun hideKeyboard() {
        val inputMM =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager  //константа для скрытия клавиатуры
        inputMM.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun init() {

        historyTracks = viewModel.getHistory()
        historyAdapter = TrackAdapter(historyTracks, object : TrackAdapter.OnClickListenerItem {
            override fun onItemClick(track: Track) {
                if (clickDebounce()) {
                    viewModel.addToHistory(track)
                    val playerIntent = Intent(this@SearchActivity, AudioPlayerActivity::class.java)
                    playerIntent.putExtra(CLICKED_ITEM, track)
                    startActivity(playerIntent)
                }
            }
        })
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.placeholder.visibility = View.GONE
        binding.rvRecycleView.visibility = View.GONE
        binding.tvSearched.visibility = View.GONE
    }

    private fun showError(errorMessage: String, errorMessageExtra: String) {
        binding.progressBar.visibility = View.GONE
        binding.rvRecycleView.visibility = View.GONE
        binding.tvSearched.visibility = View.GONE
        binding.placeholder.visibility = View.VISIBLE
        binding.ivPlaceholderImage.visibility = View.VISIBLE
        binding.tvPlaceholderMessage.visibility = View.VISIBLE
        binding.bPlaceholderUpdateBtn.visibility = View.VISIBLE
        binding.tvPlaceholderMessageExtra.visibility = View.VISIBLE
        binding.ivPlaceholderImage.setImageResource(R.drawable.light_mode_no_connection)
        binding.tvPlaceholderMessage.text = errorMessage
        binding.tvPlaceholderMessageExtra.text = errorMessageExtra

    }

    private fun showEmpty(emptyMessage: String) {
        binding.progressBar.visibility = View.GONE
        binding.rvRecycleView.visibility = View.GONE
        binding.tvSearched.visibility = View.GONE
        binding.bPlaceholderUpdateBtn.visibility = View.GONE

        binding.tvPlaceholderMessageExtra.visibility = View.GONE
        binding.placeholder.visibility = View.VISIBLE
        binding.ivPlaceholderImage.visibility = View.VISIBLE
        binding.tvPlaceholderMessage.visibility = View.VISIBLE
        binding.ivPlaceholderImage.setImageResource(R.drawable.light_mode_error)
        binding.tvPlaceholderMessage.text = emptyMessage
    }

    fun showContent(trackList: List<Track>) {

        binding.placeholder.visibility = View.GONE
        binding.bPlaceholderUpdateBtn.visibility = View.GONE
        binding.ivPlaceholderImage.visibility = View.GONE
        binding.tvPlaceholderMessage.visibility = View.GONE
        binding.tvPlaceholderMessageExtra.visibility = View.GONE
        binding.rvRecycleView.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(trackList)
        trackAdapter.notifyDataSetChanged()
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Error -> showError(state.errorMessage, state.errorMessageExtra)
            is SearchState.Empty -> showEmpty(state.message)
            is SearchState.Content -> showContent(state.tracks)
        }
    }
    private fun showHistory(){
        binding.placeholder.visibility = View.GONE
        binding.bPlaceholderUpdateBtn.visibility = View.GONE
        binding.ivPlaceholderImage.visibility = View.GONE
        binding.tvPlaceholderMessage.visibility = View.GONE
        binding.tvPlaceholderMessageExtra.visibility = View.GONE

        historyAdapter.tracks = viewModel.getHistory()
        binding.rvRecycleView.adapter = historyAdapter
        trackAdapter.notifyDataSetChanged()
        binding.rvRecycleView.visibility = View.VISIBLE
        binding.bClearHistoryBtn.visibility = View.VISIBLE
        binding.tvSearched.visibility = View.VISIBLE
    }

}
