package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.database.dao.TrackDao
import com.example.playlistmaker.data.database.entity.toDomain
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.NetworkClient
import com.example.playlistmaker.domain.TracksRepository
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.model.toEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackDao: TrackDao
) : TracksRepository {

    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as TracksSearchResponse).results.map {
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
            throw java.io.IOException("Network error")
        }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return trackDao.getTrackByNameAndArtist(track.trackName, track.artistName)
            .map { entity -> entity?.toDomain() }
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return trackDao.getFavoriteTracks().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {



        val existing = trackDao.getTrackById(track.id)
        val merged = track.copy(
            playlistId = playlistId,
            favorite = existing?.favorite ?: track.favorite,
            artworkUrl = track.artworkUrl ?: existing?.artworkUrl
        )
        trackDao.insertTrack(merged.toEntity())
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {

        val existing = trackDao.getTrackById(track.id) ?: return
        trackDao.insertTrack(existing.copy(playlistId = 0))
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        val existing = trackDao.getTrackById(track.id)
        val merged = if (existing != null) {
            existing.copy(favorite = isFavorite)
        } else {
            track.copy(favorite = isFavorite).toEntity()
        }
        trackDao.insertTrack(merged)
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        trackDao.deleteTracksByPlaylistId(playlistId)
    }
}



