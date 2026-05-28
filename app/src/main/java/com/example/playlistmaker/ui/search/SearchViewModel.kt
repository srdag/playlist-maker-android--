package com.example.playlistmaker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    private val tracksRepository: TracksRepository
) : ViewModel() {
    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState = _searchScreenState.asStateFlow()

    // Запоминаем последний запрос, чтобы кнопка «Обновить» знала, что повторить.
    private var lastQuery: String = ""

    // Активная корутина поиска — отменяем предыдущую при новом запросе,
    // чтобы старые ответы не перезаписывали актуальный результат.
    private var searchJob: Job? = null

    fun clearSearch() {
        searchJob?.cancel()
        lastQuery = ""
        _searchScreenState.update { SearchState.Initial }
    }

    fun search(whatSearch: String) {
        lastQuery = whatSearch
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                _searchScreenState.update { SearchState.Searching }
                val list = tracksRepository.searchTracks(expression = whatSearch)
                _searchScreenState.update { SearchState.Success(list = list) }
            } catch (e: IOException) {
                _searchScreenState.update { SearchState.Fail(e.message.orEmpty()) }
            } catch (e: Exception) {
                _searchScreenState.update { SearchState.Fail(e.message.orEmpty()) }
            }
        }
    }

    fun retry() {
        if (lastQuery.isNotBlank()) {
            search(lastQuery)
        }
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(Creator.getTracksRepository()) as T
                }
            }
    }
}
