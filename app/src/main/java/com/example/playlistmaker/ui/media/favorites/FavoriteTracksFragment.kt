package com.example.playlistmaker.ui.media.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.media.FavoriteTracksViewModel
import com.example.playlistmaker.ui.audioPlayer.AudioPlayerActivity
import com.example.playlistmaker.ui.media.models.FavoriteState
import com.example.playlistmaker.ui.search.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {
    companion object {
        fun newInstance() = FavoriteTracksFragment()
        const val CLICKED_ITEM = "clicked track"
        private const val CLICK_DEBOUNCE_DELAY = 2000L
    }

    private lateinit var binding: FragmentFavoriteTracksBinding
    private val viewModel by viewModel<FavoriteTracksViewModel>()
    private var adapter: TrackAdapter? = null
    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        viewModel.fillData()
        viewModel.observe().observe(viewLifecycleOwner) {
            render(it)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        adapter = null
        binding.rvRecycleView.adapter = null
    }

    private fun init() {
        adapter = TrackAdapter(object : TrackAdapter.OnClickListenerItem {

            override fun onItemClick(track: Track) {
                if (clickDebounce()) {
                    val playerIntent = Intent(requireContext(), AudioPlayerActivity::class.java)
                    playerIntent.putExtra(CLICKED_ITEM, track)
                    startActivity(playerIntent)
                }
            }
        })
        binding.rvRecycleView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvRecycleView.adapter = adapter
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

    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.Content -> showContent(state.favoriteList)
            is FavoriteState.Empty -> showEmpty(state.message)
            is FavoriteState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        binding.rvRecycleView.visibility = View.GONE
        binding.tvPlaceholderMessage.visibility = View.GONE
        binding.ivPlaceholderImage.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showEmpty(message: String) {
        binding.rvRecycleView.visibility = View.GONE
        binding.tvPlaceholderMessage.visibility = View.VISIBLE
        binding.ivPlaceholderImage.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        binding.tvPlaceholderMessage.text = message
    }

    private fun showContent(tracks: List<Track>) {
        binding.rvRecycleView.visibility = View.VISIBLE
        binding.tvPlaceholderMessage.visibility = View.GONE
        binding.ivPlaceholderImage.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        adapter?.tracks?.clear()
        adapter?.tracks?.addAll(tracks)
        adapter?.notifyDataSetChanged()
    }

}