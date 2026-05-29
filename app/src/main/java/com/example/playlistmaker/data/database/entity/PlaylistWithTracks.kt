package com.example.playlistmaker.data.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.example.playlistmaker.domain.model.Playlist


data class PlaylistWithTracks(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "playlistId"
    )
    val tracks: List<TrackEntity>
)

fun PlaylistWithTracks.toDomain() = Playlist(
    id = playlist.id,
    name = playlist.name,
    description = playlist.description,
    coverImageUri = playlist.coverImageUri,
    tracks = tracks.map { it.toDomain() }
)



