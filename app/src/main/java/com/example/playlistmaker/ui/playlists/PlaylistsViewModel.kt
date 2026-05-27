package com.example.playlistmaker.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.network.Track
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.TracksRepository
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.ui.search.Creator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val tracksRepository: TracksRepository
) : ViewModel() {

    val playlists: Flow<List<Playlist>> = playlistsRepository.getAllPlaylists()
    val favoriteList: Flow<List<Track>> = tracksRepository.getFavoriteTracks()

    fun createNewPlayList(namePlaylist: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.addNewPlaylist(namePlaylist, description)
        }
    }

    fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.insertTrackToPlaylist(track, playlistId)
        }
    }

    fun toggleFavorite(track: Track, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.updateTrackFavoriteStatus(track, isFavorite)
        }
    }

    fun deleteTrackFromPlaylist(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.deleteTrackFromPlaylist(track)
        }
    }

    fun deletePlaylist(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.deleteTracksByPlaylistId(id)
            playlistsRepository.deletePlaylistById(id)
        }
    }

    fun getPlaylist(id: Long): Flow<Playlist?> {
        return playlistsRepository.getPlaylist(id)
    }

    suspend fun isExist(track: Track): Track? {
        return tracksRepository.getTrackByNameAndArtist(track = track).firstOrNull()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlaylistsViewModel(
                        Creator.getPlaylistsRepository(),
                        Creator.getTracksRepository()
                    ) as T
                }
            }
    }
}
