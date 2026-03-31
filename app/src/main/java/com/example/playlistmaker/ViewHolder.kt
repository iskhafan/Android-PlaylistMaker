package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackArtwork: ImageView = itemView.findViewById(R.id.track_artwork)
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val trackArtist: TextView = itemView.findViewById(R.id.track_artist)
    private val trackDuration: TextView = itemView.findViewById(R.id.track_duration)

    fun bind(track: Track) {
        trackName.text = track.trackName
        trackArtist.text = track.artistName
        trackDuration.text = track.trackTime

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_track_label_placeholder_35)
            .fitCenter()
            .transform(RoundedCorners(4))
            .into(trackArtwork)
    }
}