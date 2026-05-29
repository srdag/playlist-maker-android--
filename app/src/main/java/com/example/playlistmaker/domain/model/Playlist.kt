package com.example.playlistmaker.domain.model

import androidx.compose.runtime.Immutable


@Immutable
data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val coverImageUri: String? = null,
    val tracks: List<Track> = emptyList()
)


