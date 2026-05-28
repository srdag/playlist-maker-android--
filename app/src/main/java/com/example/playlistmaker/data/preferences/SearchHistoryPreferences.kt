package com.example.playlistmaker.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SearchHistoryPreferences(
    private val dataStore: DataStore<Preferences>,
    private val coroutineScope: CoroutineScope = CoroutineScope(
        CoroutineName("search-history-preferences") + SupervisorJob()
    )
) {
    private val preferencesKey = stringPreferencesKey("search_history")

    fun addEntry(word: String) {
        if (word.isEmpty()) {
            return
        }

        coroutineScope.launch {
            dataStore.updateData { preferences ->
                val historyString = preferences[preferencesKey].orEmpty()
                val history = if (historyString.isNotEmpty()) {
                    historyString.split(SEPARATOR).toMutableList()
                } else {
                    mutableListOf()
                }

                history.remove(word) // удаляем дубликат, если был
                history.add(0, word)

                // храним не более MAX_ENTRIES элементов
                val limited = history.take(MAX_ENTRIES)
                val updatedString = limited.joinToString(SEPARATOR)

                preferences.toMutablePreferences().apply {
                    this[preferencesKey] = updatedString
                }
            }
        }
    }

    suspend fun getEntries(): List<String> {
        val preferences = dataStore.data.first()
        val historyString = preferences[preferencesKey].orEmpty()
        return if (historyString.isEmpty()) {
            emptyList()
        } else {
            historyString.split(SEPARATOR)
        }
    }

    fun clear() {
        coroutineScope.launch {
            dataStore.updateData { preferences ->
                preferences.toMutablePreferences().apply {
                    remove(preferencesKey)
                }
            }
        }
    }

    private companion object {
        const val MAX_ENTRIES = 10
        // Используем редкий символ-разделитель: запятая встречается в названиях треков.
        const val SEPARATOR = ""
    }
}
