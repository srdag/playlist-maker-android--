package com.example.playlistmaker.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.ui.search.Creator
import kotlinx.coroutines.flow.Flow

/**
 * ViewModel экрана отдельного плейлиста. Принимает id плейлиста и
 * выставляет наружу реактивный Flow с данными плейлиста, который
 * автоматически обновляется при изменениях в базе.
 */
class PlaylistViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val playlistId: Long
) : ViewModel() {

    val playlist: Flow<Playlist?> = playlistsRepository.getPlaylist(playlistId)

    companion object {
        fun getViewModelFactory(playlistId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlaylistViewModel(
                        Creator.getPlaylistsRepository(),
                        playlistId
                    ) as T
                }
            }
    }
}
