package com.example.playlistmaker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.SearchHistoryRepository
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    private val tracksRepository: TracksRepository,
    private val historyRepository: SearchHistoryRepository
) : ViewModel() {
    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState = _searchScreenState.asStateFlow()



    private val _historyState = MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val historyState = _historyState.asStateFlow()


    private var lastQuery: String = ""



    private var searchJob: Job? = null

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            _historyState.update { historyRepository.getHistory().toImmutableList() }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        lastQuery = ""
        _searchScreenState.update { SearchState.Initial }
        loadHistory()
    }

    fun search(whatSearch: String) {
        lastQuery = whatSearch
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                _searchScreenState.update { SearchState.Searching }
                val list = tracksRepository.searchTracks(expression = whatSearch)
                _searchScreenState.update { SearchState.Success(list = list.toImmutableList()) }
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


    fun saveQueryToHistory(query: String) {
        if (query.isBlank()) return
        historyRepository.addQuery(query.trim())
        loadHistory()
    }

    fun clearHistory() {
        historyRepository.clear()
        loadHistory()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(
                        Creator.getTracksRepository(),
                        Creator.getSearchHistoryRepository()
                    ) as T
                }
            }
    }
}



