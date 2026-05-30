package com.example.playlistmaker.data.database

import com.example.playlistmaker.data.database.entity.TrackEntity
import com.example.playlistmaker.domain.model.Track


fun Track.toEntity(): TrackEntity = TrackEntity(
    id = id,
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    artworkUrl = artworkUrl,
    favorite = favorite,
    playlistId = playlistId
)



