package com.example.playlistmaker.domain.model

import androidx.compose.runtime.Immutable
import com.example.playlistmaker.data.database.entity.TrackEntity


@Immutable
data class Track(
    val id: Long = 0,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl: String? = null,
    val favorite: Boolean = false,
    val playlistId: Long = 0
)

fun Track.toEntity() = TrackEntity(
    id = id,
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    artworkUrl = artworkUrl,
    favorite = favorite,
    playlistId = playlistId
)


