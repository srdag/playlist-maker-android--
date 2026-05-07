package com.example.playlistmaker.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.network.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.network.Track
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.data.storage.DatabaseMock
import com.example.playlistmaker.data.storage.Playlist
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class PlaylistsViewModel() : ViewModel() {
    private val playlistsRepository: PlaylistsRepository =
        PlaylistsRepositoryImpl(scope = viewModelScope)
    private val tracksRepository: TracksRepository = TracksRepositoryImpl(scope = viewModelScope)

    private val databaseRepository: DatabaseMock = DatabaseMock(scope = viewModelScope)

    val playlists: Flow<List<Playlist>> = flow {
        val collectedPlaylists = mutableListOf<Playlist>()
        playlistsRepository.getAllPlaylists().collect { playlist ->
            collectedPlaylists.addAll(playlist)
            emit(collectedPlaylists.toList())
        }
    }
    val favoriteList: Flow<List<Track>> = databaseRepository.getFavoriteTracks()

    fun createNewPlayList(namePlaylist: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.addNewPlaylist(namePlaylist, description)
        }
    }

    suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        tracksRepository.insertTrackToPlaylist(track, playlistId)
    }

    suspend fun toggleFavorite(track: Track, isFavorite: Boolean) {
        tracksRepository.updateTrackFavoriteStatus(track, isFavorite)
    }

    suspend fun deleteTrackFromPlaylist(track: Track) {
        tracksRepository.deleteTrackFromPlaylist(track)
    }

    suspend fun deletePlaylistById(id: Long) {
        tracksRepository.deleteTracksByPlaylistId(id)
        playlistsRepository.deletePlaylistById(id)
    }

    suspend fun isExist(track: Track): Track? {
        return tracksRepository.getTrackByNameAndArtist(track = track).firstOrNull()
    }
}