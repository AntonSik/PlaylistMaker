package com.example.playlistmaker.ui.search


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(

    private val listenerItem: OnClickListenerItem
) : RecyclerView.Adapter<TrackViewHolder>() {
    var tracks = ArrayList<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        return TrackViewHolder(
            ItemTrackBinding.inflate(layoutInflater, parent, false),

        )

    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {

        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { listenerItem.onItemClick(tracks.get(position)) }
    }

   fun interface OnClickListenerItem {
        fun onItemClick(track: Track)

    }

}