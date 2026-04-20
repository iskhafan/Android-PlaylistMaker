package com.example.playlistmaker
import java.io.Serializable

data class Track (
    val trackName: String,
    val artistName: String,
    val trackTime: Long,
    val artworkUrl100: String?,
    val trackId: Int,
    val collectionName: String? = null,
    val releaseDate: String? = null,
    val primaryGenreName: String? = null,
    val country: String? = null
) : Serializable {
    // Pulling cover with higher resolution
    fun getCoverArtwork(): String? {
        return artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
    }
}
