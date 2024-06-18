package com.example.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
//import com.example.playlistmaker.OnClickListenerItem
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(
    var tracks: ArrayList<Track>,
    val listenerItem: OnClickListenerItem
) : RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
//        return TrackViewHolder(view,listenerItem)
        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackViewHolder(ItemTrackBinding.inflate(layoutInspector, parent,false), listenerItem)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
//        holder.bind(tracks.get(position))
        holder.bind(tracks.get(position))
    }
    interface OnClickListenerItem {
        fun onItemClick(track: Track)

    }

}