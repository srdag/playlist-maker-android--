package com.example.playlistmaker.domain

import com.example.playlistmaker.data.network.Track

interface TracksRepository {
    suspend fun searchTracks(expression: String): List<Track>
}
