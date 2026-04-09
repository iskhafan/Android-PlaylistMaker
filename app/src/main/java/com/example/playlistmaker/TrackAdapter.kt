package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface OnTrackClickListener {
    fun onTrackClicked(track: Track)
}

class TrackAdapter(
    private var items: List<Track> = emptyList(),
    private val listener: OnTrackClickListener
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track,
            parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val currentTrack = items[position]
        holder.bind(currentTrack)

        // Handling track list item press
        holder.itemView.setOnClickListener {
            listener.onTrackClicked(currentTrack)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(newItems: List<Track>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}