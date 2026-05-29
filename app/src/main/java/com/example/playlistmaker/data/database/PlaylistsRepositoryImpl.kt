package com.example.playlistmaker.data.database

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.data.database.dao.TrackDao
import com.example.playlistmaker.data.database.entity.PlaylistEntity
import com.example.playlistmaker.data.database.entity.toDomain
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

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

    override suspend fun addNewPlaylist(
        context: Context,
        name: String,
        description: String,
        coverImageUri: String?
    ) {
        val savedUri = coverImageUri?.let { saveImageToInternalStorage(context, Uri.parse(it)) }
        playlistDao.insertPlaylist(
            PlaylistEntity(
                name = name,
                description = description,
                coverImageUri = savedUri?.toString()
            )
        )
    }

    private fun saveImageToInternalStorage(context: Context, uri: Uri): Uri? {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "cover_${UUID.randomUUID()}.jpg")
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            Uri.fromFile(file)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deletePlaylistById(id: Long) {


        trackDao.deleteTracksByPlaylistId(id)
        playlistDao.deletePlaylistById(id)
    }
}



