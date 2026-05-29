package com.example.playlistmaker.ui.search

import androidx.compose.runtime.Immutable
import com.example.playlistmaker.domain.model.Track
import kotlinx.collections.immutable.ImmutableList


@Immutable
sealed class SearchState {
    data object Initial : SearchState() // Первоначальное состояние экрана
    data object Searching : SearchState() // Состояние экрана при начале поиска
    data class Success(val list: ImmutableList<Track>) : SearchState() // Успешный поиск
    data class Fail(val error: String) : SearchState() // Ошибка запроса
}


