package com.example.playlistmaker.ui.media.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.media.PlaylistsViewModel
import com.example.playlistmaker.ui.media.models.PlaylistState
import com.example.playlistmaker.ui.openedPlaylist.OpenedPlaylistFragment
import com.example.playlistmaker.ui.root.BottomNavBarShower
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    companion object {
        fun newInstance() = PlaylistsFragment()
        const val PREVIOUS_SCREEN = "previous screen"
        const val PREVIOUS_SCREEN_IS_PLAYLIST = "PlaylistsFragment"
        const val CLICKED_PLAYLIST_ID = "selectedPlaylist"
        private const val CLICK_DEBOUNCE_DELAY = 2000L
    }

    private lateinit var binding: FragmentPlaylistsBinding
    private var adapter: PlaylistAdapter? = null
    private val viewModel by viewModel<PlaylistsViewModel>()
    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        viewModel.fillData()
        viewModel.stateLive.observe(viewLifecycleOwner) {
            render(it)
        }

        binding.bNewPlaylistBtn.setOnClickListener {
            (activity as? BottomNavBarShower)?.hideNavBar()

            val fragment = PlaylistsCreatorFragment.newInstance().apply {
                arguments = Bundle().apply {
                    putString(PREVIOUS_SCREEN, PREVIOUS_SCREEN_IS_PLAYLIST)
                }
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.rootFragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        adapter = null
        binding.rvRecyclerViewPlaylist.adapter = null
    }

    private fun init() {
        isClickAllowed = true
        adapter = PlaylistAdapter(object : PlaylistAdapter.OnClickListenerItem {
            override fun onItemClick(playlist: Playlist) {
                if (clickDebounce()) {

                    (activity as? BottomNavBarShower)?.hideNavBar()
                    val fragment = OpenedPlaylistFragment.newInstance().apply {
                        arguments = Bundle().apply {
                            putInt(CLICKED_PLAYLIST_ID, playlist.playlistId)
                        }
                    }
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.rootFragmentContainerView, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }

        })
        binding.rvRecyclerViewPlaylist.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvRecyclerViewPlaylist.adapter = adapter
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

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> showContent(state.playlists)
            is PlaylistState.Empty -> showEmpty(state.message)
            PlaylistState.Loading -> showLoading()
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.progressBar.visibility = View.GONE
        binding.ivPlaceholderImage.visibility = View.GONE
        binding.tvPlaceholderMessage.visibility = View.GONE

        binding.rvRecyclerViewPlaylist.visibility = View.VISIBLE
        adapter?.playlists?.clear()
        adapter?.playlists?.addAll(playlists)
        adapter?.notifyDataSetChanged()
    }

    private fun showEmpty(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.rvRecyclerViewPlaylist.visibility = View.GONE

        binding.ivPlaceholderImage.visibility = View.VISIBLE
        binding.tvPlaceholderMessage.visibility = View.VISIBLE
        binding.tvPlaceholderMessage.text = message

    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.ivPlaceholderImage.visibility = View.GONE
        binding.tvPlaceholderMessage.visibility = View.GONE
        binding.rvRecyclerViewPlaylist.visibility = View.GONE
    }
}