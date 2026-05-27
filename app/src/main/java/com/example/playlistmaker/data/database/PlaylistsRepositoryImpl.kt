package com.example.playlistmaker.data.database

import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistsRepositoryImpl(
    private val database: DatabaseMock
) : PlaylistsRepository {

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return database.getPlaylist(playlistId)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return database.getAllPlaylists()
    }

    override suspend fun addNewPlaylist(name: String, description: String) {
        database.addNewPlaylist(
            name = name,
            description = description
        )
    }

    override suspend fun deletePlaylistById(id: Long) {
        database.deletePlaylistById(playlistId = id)
    }
}
