package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.database.entity.TrackEntity

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
