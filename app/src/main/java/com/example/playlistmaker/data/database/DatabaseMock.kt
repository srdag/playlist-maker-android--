package com.example.playlistmaker.data.database

import com.example.playlistmaker.data.network.Track
import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DatabaseMock(
    private val scope: CoroutineScope,
) {
    private val historyList = mutableListOf<String>()
    private val _historyUpdates = MutableSharedFlow<Unit>()

    private val playlists = mutableListOf<Playlist>()
    private val tracks = mutableListOf<Track>()

    // Триггеры реактивных обновлений: каждое изменение увеличивает значение,
    // что приводит к повторному эмиту во всех Flow, подписанных на эти StateFlow.
    private val _playlistsTrigger = MutableStateFlow(0)
    private val _tracksTrigger = MutableStateFlow(0)

    fun getHistory(): List<String> {
        return historyList.toList()
    }

    fun addToHistory(word: String) {
        historyList.add(word)
        notifyHistoryChanged()
    }

    private fun notifyHistoryChanged() {
        scope.launch(Dispatchers.IO) {
            _historyUpdates.emit(Unit)
        }
    }

    private fun notifyPlaylistsChanged() {
        _playlistsTrigger.update { it + 1 }
    }

    private fun notifyTracksChanged() {
        _tracksTrigger.update { it + 1 }
        // Изменение треков может затрагивать списки в плейлистах,
        // поэтому также триггерим обновление плейлистов.
        _playlistsTrigger.update { it + 1 }
    }

    fun getAllPlaylists(): Flow<List<Playlist>> = _playlistsTrigger.map {
        delay(300) // Имитируем задержку загрузки из базы данных
        playlists.map { playlist ->
            playlist.copy(tracks = tracks.filter { track -> track.playlistId == playlist.id })
        }.toList()
    }

    fun getPlaylist(id: Long): Flow<Playlist?> = _playlistsTrigger.map {
        val playlist = playlists.find { it.id == id }
        playlist?.copy(tracks = tracks.filter { it.playlistId == id })
    }

    fun addNewPlaylist(name: String, description: String) {
        playlists.add(
            Playlist(
                id = playlists.size.toLong() + 1,
                name = name,
                description = description,
                tracks = emptyList()
            )
        )
        notifyPlaylistsChanged()
    }

    fun deletePlaylistById(playlistId: Long) {
        playlists.removeIf { it.id == playlistId }
        notifyPlaylistsChanged()
    }

    fun deleteTrackFromPlaylist(trackId: Long) {
        tracks.removeIf { it.id == trackId }
        notifyTracksChanged()
    }

    fun getTrackByNameAndArtist(track: Track): Flow<Track?> = _tracksTrigger.map {
        tracks.find {
            it.trackName == track.trackName && it.artistName == track.artistName
        }
    }

    fun insertTrack(track: Track) {
        tracks.removeIf { it.id == track.id }
        tracks.add(track)
        notifyTracksChanged()
    }

    fun getFavoriteTracks(): Flow<List<Track>> = _tracksTrigger.map {
        delay(200) // Имитируем задержку
        tracks.filter { it.favorite }
    }

    fun deleteTracksByPlaylistId(playlistId: Long) {
        tracks.removeIf { it.playlistId == playlistId }
        notifyTracksChanged()
    }

    fun searchTracks(expression: String): List<Track> {
        return tracks.filter { it.trackName.contains(expression, true) }
    }
}
