package com.example.playlistmaker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.data.database.entity.PlaylistEntity
import com.example.playlistmaker.data.database.entity.PlaylistWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlist WHERE id = :id")
    suspend fun deletePlaylistById(id: Long)

    @Transaction
    @Query("SELECT * FROM playlist ORDER BY id ASC")
    fun getAllPlaylistsWithTracks(): Flow<List<PlaylistWithTracks>>

    /**
     * Возвращаем список, а не nullable-объект, потому что Flow<T?> с @Relation
     * исторически работает нестабильно. На стороне репозитория берём firstOrNull().
     */
    @Transaction
    @Query("SELECT * FROM playlist WHERE id = :id LIMIT 1")
    fun getPlaylistWithTracks(id: Long): Flow<List<PlaylistWithTracks>>
}
