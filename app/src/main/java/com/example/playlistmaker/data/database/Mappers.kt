package com.example.playlistmaker.data.database

import com.example.playlistmaker.data.database.entity.TrackEntity
import com.example.playlistmaker.data.network.Track

/**
 * Конвертер из доменной модели Track в Room-сущность.
 * Обратный конвертер (TrackEntity.toDomain) живёт прямо в файле TrackEntity.kt.
 */
fun Track.toEntity(): TrackEntity = TrackEntity(
    id = id,
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    artworkUrl = artworkUrl,
    favorite = favorite,
    playlistId = playlistId
)
