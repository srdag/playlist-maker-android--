package com.example.playlistmaker.data.database

import com.example.playlistmaker.data.database.dao.TrackDao
import com.example.playlistmaker.data.database.entity.PlaylistEntity
import com.example.playlistmaker.data.database.entity.toDomain
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    database: AppDatabase,
    private val trackDao: TrackDao = database.trackDao(),
) : PlaylistsRepository {

    private val playlistDao = database.playlistDao()

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return playlistDao.getPlaylistWithTracks(playlistId).map { list ->
            list.firstOrNull()?.toDomain()
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylistsWithTracks().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addNewPlaylist(name: String, description: String, coverImageUri: String?) {
        playlistDao.insertPlaylist(
            PlaylistEntity(
                name = name,
                description = description,
                coverImageUri = coverImageUri
            )
        )
    }

    override suspend fun deletePlaylistById(id: Long) {


        trackDao.deleteTracksByPlaylistId(id)
        playlistDao.deletePlaylistById(id)
    }
}



