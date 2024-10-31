package com.example.playlistmaker.ui.media.favorites


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.media.FavoriteTracksViewModel
import com.example.playlistmaker.ui.audioPlayer.AudioPlayerFragment
import com.example.playlistmaker.ui.media.favorites.models.FavoriteState
import com.example.playlistmaker.ui.root.BottomNavBarShower
import com.example.playlistmaker.ui.search.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {
    companion object {
        fun newInstance() = FavoriteTracksFragment()
        const val CLICKED_ITEM = "clicked track"
    }

    private lateinit var binding: FragmentFavoriteTracksBinding
    private val viewModel by viewModel<FavoriteTracksViewModel>()
    private var adapter: TrackAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? BottomNavBarShower)?.showNavbar()
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

                val fragment = AudioPlayerFragment.newInstance().apply {
                    arguments = Bundle().apply {
                        putParcelable(CLICKED_ITEM, track)

                    }
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.rootFragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        })
        binding.rvRecycleView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvRecycleView.adapter = adapter
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