package com.example.playlistmaker.ui.search

import com.example.playlistmaker.creator.Storage
import com.example.playlistmaker.data.database.DatabaseMock
import com.example.playlistmaker.data.database.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object Creator {
    // Глобальный CoroutineScope, в котором живёт мок-база и её триггеры.
    // SupervisorJob позволяет одной упавшей корутине не отменять весь scope.
    private val databaseScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Единый экземпляр базы. Все репозитории работают с одним и тем же стораджем,
    // иначе данные между плейлистами и треками не синхронизировались бы.
    private val database: DatabaseMock by lazy { DatabaseMock(databaseScope) }

    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(Storage()), database)
    }

    fun getPlaylistsRepository(): PlaylistsRepository {
        return PlaylistsRepositoryImpl(database)
    }
}
