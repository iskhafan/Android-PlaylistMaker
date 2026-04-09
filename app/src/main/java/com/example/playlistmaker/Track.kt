package com.example.playlistmaker

data class Track (
    val trackName: String,
    val artistName: String,
    val trackTime: Long,
//NOTE: accepting invalid empty string response
    val artworkUrl100: String?
)
