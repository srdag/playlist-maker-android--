package com.example.playlistmaker.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class ThemeViewModel : ViewModel() {
    private val _darkTheme = MutableStateFlow(false)
    val darkTheme = _darkTheme.asStateFlow()

    fun setDarkTheme(value: Boolean) {
        _darkTheme.value = value
    }
}



