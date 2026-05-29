package com.example.playlistmaker.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.domain.model.Track

@Entity(tableName = "track")
data class TrackEntity(
    @PrimaryKey val id: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl: String?,
    val favorite: Boolean = false,
    val playlistId: Long = 0
)

fun TrackEntity.toDomain() = Track(
    id = id,
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    artworkUrl = artworkUrl,
    favorite = favorite,
    playlistId = playlistId
)



