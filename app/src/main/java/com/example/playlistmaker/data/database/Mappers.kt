package com.example.playlistmaker.data.database

import com.example.playlistmaker.data.database.entity.PlaylistEntity
import com.example.playlistmaker.data.database.entity.PlaylistWithTracks
import com.example.playlistmaker.data.database.entity.TrackEntity
import com.example.playlistmaker.data.network.Track
import com.example.playlistmaker.domain.model.Playlist

fun TrackEntity.toDomain(): Track = Track(
    id = id,
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    artworkUrl = artworkUrl,
    favorite = favorite,
    playlistId = playlistId
)

fun Track.toEntity(): TrackEntity = TrackEntity(
    id = id,
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    artworkUrl = artworkUrl,
    favorite = favorite,
    playlistId = playlistId
)

fun PlaylistEntity.toDomain(tracks: List<Track> = emptyList()): Playlist = Playlist(
    id = id,
    name = name,
    description = description,
    tracks = tracks
)

fun PlaylistWithTracks.toDomain(): Playlist = playlist.toDomain(
    tracks = tracks.map { it.toDomain() }
)
