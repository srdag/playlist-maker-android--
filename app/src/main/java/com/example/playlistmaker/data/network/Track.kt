package com.example.playlistmaker.data.network

data class Track(
    val trackName: String,
    val id: Long,
    val favorite: Boolean,
    val artistName: String,
    val trackTime: String,
    val playlistId: Long,
)
