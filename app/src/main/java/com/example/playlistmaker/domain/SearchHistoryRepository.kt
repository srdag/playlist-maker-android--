package com.example.playlistmaker.domain

interface SearchHistoryRepository {
    suspend fun getHistory(): List<String>
    fun addQuery(query: String)
    fun clear()
}



