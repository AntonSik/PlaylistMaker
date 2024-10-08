package com.example.playlistmaker.ui.media.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.media.PlaylistsViewModel
import com.example.playlistmaker.ui.media.models.PlaylistState
import com.example.playlistmaker.ui.root.BottomNavBarShower
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    companion object {
        fun newInstance() = PlaylistsFragment()
        const val PREVIOUS_SCREEN = "previous screen"
    }

    private lateinit var binding: FragmentPlaylistsBinding
    private var adapter: PlaylistAdapter? = null
    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvRecyclerViewPlaylist.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = PlaylistAdapter()
        binding.rvRecyclerViewPlaylist.adapter = adapter

        viewModel.fillData()
        viewModel.observe().observe(viewLifecycleOwner){
            render(it)
        }

        binding.bNewPlaylistBtn.setOnClickListener {
            (activity as? BottomNavBarShower)?.hideNavBar()
            val fragment = PlaylistsCreatorFragment.newInstance().apply {
                arguments = Bundle().apply {
                    putString(PREVIOUS_SCREEN,"PlaylistsFragment")
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
    private fun showEmpty(message:String) {
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