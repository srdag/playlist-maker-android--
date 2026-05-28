package com.example.playlistmaker.navigation

enum class Screens(val route: String) {
    MainScreen("main_screen"),
    SettingsScreen("settings_screen"),
    FinderScreen("finder_screen"),
    PlaylistsScreen("playlists_screen"),
    FavoritesScreen("favorites_screen"),
    NewPlaylistScreen("new_playlist_screen"),

    // Маршрут отдельного плейлиста — принимает id.
    PlaylistScreen("playlist_screen/{playlistId}"),

    // Параметры передаются как path-arguments
    TrackDetailsScreen("track_details_screen/{trackName}/{artistName}/{trackTime}/{artworkUrl}"),
}
