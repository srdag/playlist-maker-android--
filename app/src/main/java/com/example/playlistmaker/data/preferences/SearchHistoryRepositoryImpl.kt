package com.example.playlistmaker.data.preferences

import com.example.playlistmaker.domain.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val preferences: SearchHistoryPreferences
) : SearchHistoryRepository {

    override suspend fun getHistory(): List<String> = preferences.getEntries()

    override fun addQuery(query: String) = preferences.addEntry(query)

    override fun clear() = preferences.clear()
}
