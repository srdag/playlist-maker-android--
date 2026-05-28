package com.example.playlistmaker.data.dto

data class TrackDto(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String? = null
)
