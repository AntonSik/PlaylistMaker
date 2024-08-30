package com.example.playlistmaker.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.SearchTracksViewModel
import com.example.playlistmaker.ui.audioPlayer.AudioPlayerActivity
import com.example.playlistmaker.ui.search.models.SearchState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    companion object {
        const val CLICKED_ITEM = "clicked track"
        private const val CLICK_DEBOUNCE_DELAY = 2000L
    }

    private lateinit var binding: FragmentSearchBinding

    private var savedText: String? = null
    private var isClickAllowed = true
    private val viewModel by viewModel<SearchTracksViewModel>()

    private lateinit var trackAdapter: TrackAdapter
    private var textWatcher: TextWatcher? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            trackAdapter.tracks.clear()
            trackAdapter.notifyDataSetChanged()

        }
        binding.bClearHistoryBtn.setOnClickListener {
            viewModel.clearHistory()
            trackAdapter.tracks.clear()
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
                if (s?.isNotEmpty() == true) {
                    clearAdapter()
                } else if (s?.isEmpty() == true) {
                    viewModel.setHistoryFlag(true)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        textWatcher?.let { binding.inputEditText.addTextChangedListener(it) }

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->

            when {
                !hasFocus && binding.inputEditText.text.isEmpty() -> clearAdapter()
                hasFocus && binding.inputEditText.text.isEmpty() && viewModel.getHistory()
                     .isNotEmpty() -> viewModel.setHistoryFlag(true)

                hasFocus && binding.inputEditText.text.isNotEmpty() -> viewModel.setHistoryFlag(
                    false
                )

                !hasFocus && binding.inputEditText.text.isNotEmpty() -> viewModel.setHistoryFlag(
                    false
                )

                hasFocus && binding.inputEditText.text.isEmpty() && viewModel.getHistory()
                    .isNotEmpty() -> clearAdapter()

            }
        }
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.trackListLiveData.observe(viewLifecycleOwner) { trackListLive ->
            trackAdapter.tracks = ArrayList(trackListLive)
            trackAdapter.notifyDataSetChanged()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        textWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun init() {

        trackAdapter = TrackAdapter(object : TrackAdapter.OnClickListenerItem {

            override fun onItemClick(track: Track) {
                if (clickDebounce()) {
                    viewModel.addToHistory(track)
                    val playerIntent = Intent(requireContext(), AudioPlayerActivity::class.java)
                    playerIntent.putExtra(CLICKED_ITEM, track)
                    startActivity(playerIntent)
                }
            }
        })

        if (viewModel.trackListLiveData.value != null) {
            trackAdapter.tracks = ArrayList(viewModel.trackListLiveData.value!!)
        }
        binding.rvRecycleView.adapter = trackAdapter

    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.placeholder.visibility = View.GONE
        binding.rvRecycleView.visibility = View.GONE
        binding.tvSearched.visibility = View.GONE
        binding.bClearHistoryBtn.visibility = View.GONE
    }

    private fun showError(@StringRes errorMessage: Int, @StringRes errorMessageExtra: Int) {
        binding.progressBar.visibility = View.GONE
        binding.rvRecycleView.visibility = View.GONE
        binding.tvSearched.visibility = View.GONE
        binding.placeholder.visibility = View.VISIBLE
        binding.ivPlaceholderImage.visibility = View.VISIBLE
        binding.tvPlaceholderMessage.visibility = View.VISIBLE
        binding.bPlaceholderUpdateBtn.visibility = View.VISIBLE
        binding.tvPlaceholderMessageExtra.visibility = View.VISIBLE
        binding.ivPlaceholderImage.setImageResource(R.drawable.light_mode_no_connection)
        binding.tvPlaceholderMessage.text = getString(errorMessage)
        binding.tvPlaceholderMessageExtra.text = getString(errorMessageExtra)

    }

    private fun showEmpty(@StringRes emptyMessage: Int) {
        binding.progressBar.visibility = View.GONE
        binding.rvRecycleView.visibility = View.GONE
        binding.tvSearched.visibility = View.GONE
        binding.bPlaceholderUpdateBtn.visibility = View.GONE

        binding.tvPlaceholderMessageExtra.visibility = View.GONE
        binding.placeholder.visibility = View.VISIBLE
        binding.ivPlaceholderImage.visibility = View.VISIBLE
        binding.tvPlaceholderMessage.visibility = View.VISIBLE
        binding.ivPlaceholderImage.setImageResource(R.drawable.light_mode_error)
        binding.tvPlaceholderMessage.text = getString(emptyMessage)
    }

    private fun showContent(trackList: List<Track>, isHistoryFlag: Boolean) {

        binding.placeholder.visibility = View.GONE
        binding.bPlaceholderUpdateBtn.visibility = View.GONE
        binding.ivPlaceholderImage.visibility = View.GONE
        binding.tvPlaceholderMessage.visibility = View.GONE
        binding.tvPlaceholderMessageExtra.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.rvRecycleView.visibility = View.VISIBLE

        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(trackList)
        trackAdapter.notifyDataSetChanged()


        if (isHistoryFlag) {

            binding.bClearHistoryBtn.visibility = View.VISIBLE
            binding.tvSearched.visibility = View.VISIBLE

        } else {

            binding.bClearHistoryBtn.visibility = View.GONE
            binding.tvSearched.visibility = View.GONE

        }
    }

    private fun clearAdapter() {
        binding.bClearHistoryBtn.visibility = View.GONE
        binding.tvSearched.visibility = View.GONE
        trackAdapter.tracks.clear()
        trackAdapter.notifyDataSetChanged()
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Error -> showError(state.errorMessage, state.errorMessageExtra)
            is SearchState.Empty -> showEmpty(state.message)
            is SearchState.Content -> showContent(state.tracks, state.isHistory)
            is SearchState.History -> showContent(state.history, state.isHistory)
        }
    }



}