package com.example.playlistmaker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.database.dao.PlaylistDao
import com.example.playlistmaker.data.database.dao.TrackDao
import com.example.playlistmaker.data.database.entity.PlaylistEntity
import com.example.playlistmaker.data.database.entity.TrackEntity

@Database(
    entities = [
        PlaylistEntity::class,
        TrackEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackDao(): TrackDao
}
