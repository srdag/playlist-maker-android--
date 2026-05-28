package com.example.playlistmaker.domain.model

import com.example.playlistmaker.data.network.Track

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val coverImageUri: String? = null,
    var tracks: List<Track> = emptyList()
)
