package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.database.DatabaseMock
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.NetworkClient
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val database: DatabaseMock
) : TracksRepository {

    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        delay(1000) // Эммулируем задержку ответа
        return if (response.resultCode == 200) { // успешный запрос
            (response as TracksSearchResponse).results.map {
                val seconds = it.trackTimeMillis / 1000
                val minutes = seconds / 60
                val trackTime =
                    "%02d".format(minutes) + ":" + "%02d".format(seconds - minutes * 60)
                Track(
                    id = (it.trackName + it.artistName).hashCode().toLong(),
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTime = trackTime,
                    artworkUrl = it.artworkUrl100
                )
            }
        } else {
            emptyList()
        }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return database.getTrackByNameAndArtist(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return database.getFavoriteTracks()
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        database.insertTrack(track.copy(playlistId = playlistId))
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        database.insertTrack(track.copy(playlistId = 0))
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        database.insertTrack(track.copy(favorite = isFavorite))
    }

    override fun deleteTracksByPlaylistId(playlistId: Long) {
        database.deleteTracksByPlaylistId(playlistId)
    }
}
