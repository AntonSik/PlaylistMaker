package com.example.playlistmaker.ui.media.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.domain.models.Playlist


class PlaylistAdapter(
    private val listenerItem: OnClickListenerItem
) : RecyclerView.Adapter<PlaylistViewHolder>() {
    var playlists = ArrayList<Playlist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(
            ItemPlaylistBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { listenerItem.onItemClick(playlists[position]) }
    }

    fun interface OnClickListenerItem {
        fun onItemClick(playlist: Playlist)
    }
}