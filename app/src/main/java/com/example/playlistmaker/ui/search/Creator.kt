package com.example.playlistmaker.ui.search

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.playlistmaker.creator.Storage
import com.example.playlistmaker.data.database.AppDatabase
import com.example.playlistmaker.data.database.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.data.preferences.SearchHistoryPreferences
import com.example.playlistmaker.data.preferences.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.SearchHistoryRepository
import com.example.playlistmaker.domain.TracksRepository


private val Context.searchHistoryDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "search_history"
)

object Creator {

    private lateinit var appDatabase: AppDatabase
    private lateinit var historyPreferences: SearchHistoryPreferences


    fun init(context: Context) {
        appDatabase = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "playlist-maker.db"
        )



            .fallbackToDestructiveMigration()
            .build()

        historyPreferences = SearchHistoryPreferences(
            dataStore = context.applicationContext.searchHistoryDataStore
        )
    }

    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(
            networkClient = RetrofitNetworkClient(Storage()),
            trackDao = appDatabase.trackDao()
        )
    }

    fun getPlaylistsRepository(): PlaylistsRepository {
        return PlaylistsRepositoryImpl(appDatabase)
    }

    fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(historyPreferences)
    }
}



