package com.example.playlistmaker.ui.media.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.presentation.media.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private lateinit var binding: FragmentPlaylistsBinding
    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }
}