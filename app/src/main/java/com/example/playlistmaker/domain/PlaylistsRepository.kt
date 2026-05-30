package com.example.playlistmaker.domain

import android.content.Context
import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylist(playlistId: Long): Flow<Playlist?>

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun addNewPlaylist(
        context: Context,
        name: String,
        description: String,
        coverImageUri: String? = null
    )

    suspend fun deletePlaylistById(id: Long)
}



