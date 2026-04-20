package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Extracting track info
        val track = intent.getSerializableExtra(EXTRA_TRACK) as? Track
        if (track == null) {
            finish()
            return
        }

        // Back button
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        // Track info title
        findViewById<TextView>(R.id.title_text).text = track.trackName
        findViewById<TextView>(R.id.artist_text).text = track.artistName

        // Show cover
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_track_label_placeholder_35)
            .error(R.drawable.ic_track_label_placeholder_35)
            .transform(RoundedCorners(8))
            .into(findViewById(R.id.cover_image))

        val durationFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        findViewById<TextView>(R.id.duration_value).text = durationFormatter.format(Date(track.trackTime))

        // Conditional track info draw
        setViewVisibility(R.id.album_label, R.id.album_value, track.collectionName)
        setViewVisibility(R.id.year_label, R.id.year_value, track.releaseDate?.take(4))
        setViewVisibility(R.id.genre_label, R.id.genre_value, track.primaryGenreName)
        setViewVisibility(R.id.country_label, R.id.country_value, track.country)
    }

    private fun setViewVisibility(labelId: Int, valueId: Int, text: String?) {
        findViewById<TextView>(valueId).text = text
        val isVisible = !text.isNullOrEmpty()
        findViewById<TextView>(labelId).visibility = if (isVisible) View.VISIBLE else View.GONE
        findViewById<TextView>(valueId).visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_TRACK = "extra_track"
    }
}
