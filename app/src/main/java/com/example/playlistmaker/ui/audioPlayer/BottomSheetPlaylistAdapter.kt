package com.example.playlistmaker.ui.audioPlayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.example.playlistmaker.domain.models.Playlist

class BottomSheetPlaylistAdapter(
    private val listenerItem: OnClickListenerItem
) : RecyclerView.Adapter<BottomSheetPlaylistViewHolder>() {
    var playlists = ArrayList<Playlist>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomSheetPlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return BottomSheetPlaylistViewHolder(
            ItemPlaylistBottomSheetBinding.inflate(layoutInflater, parent, false),
        )
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: BottomSheetPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { listenerItem.onItemClick(playlists[position]) }
    }

    fun interface OnClickListenerItem {
        fun onItemClick(playlist: Playlist)

    }
}