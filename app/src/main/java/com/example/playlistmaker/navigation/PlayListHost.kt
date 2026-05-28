package com.example.playlistmaker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.playlistmaker.SettingsScreen
import com.example.playlistmaker.data.network.Track
import com.example.playlistmaker.ui.activity.MainScreen
import com.example.playlistmaker.ui.playlists.FavoritesScreen
import com.example.playlistmaker.ui.playlists.NewPlaylistScreen
import com.example.playlistmaker.ui.playlists.PlaylistScreen
import com.example.playlistmaker.ui.playlists.PlaylistViewModel
import com.example.playlistmaker.ui.playlists.PlaylistsScreen
import com.example.playlistmaker.ui.playlists.PlaylistsViewModel
import com.example.playlistmaker.ui.search.FinderScreen
import com.example.playlistmaker.ui.search.SearchViewModel
import com.example.playlistmaker.ui.track.TrackDetailsScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// Сентинел для отсутствующего artworkUrl: пустой path-сегмент в URL вызывает
// проблемы у NavController, поэтому передаём строку-маркер.
private const val ARTWORK_NONE = "__none__"

@Composable
fun PlaylistHost(
    navController: NavHostController,
    searchViewModel: SearchViewModel,
    playlistsViewModel: PlaylistsViewModel,
) {
    // Хелпер для безопасного формирования маршрута в Track details:
    // имена треков и артистов могут содержать пробелы, кириллицу, спецсимволы.
    fun navigateToTrack(track: Track) {
        val name = URLEncoder.encode(track.trackName, StandardCharsets.UTF_8.toString())
        val artist = URLEncoder.encode(track.artistName, StandardCharsets.UTF_8.toString())
        val time = URLEncoder.encode(track.trackTime, StandardCharsets.UTF_8.toString())
        val art = if (track.artworkUrl.isNullOrBlank()) {
            ARTWORK_NONE
        } else {
            URLEncoder.encode(track.artworkUrl, StandardCharsets.UTF_8.toString())
        }
        navController.navigate("track_details_screen/$name/$artist/$time/$art")
    }

    NavHost(navController, Screens.MainScreen.route) {
        composable(Screens.MainScreen.route) {
            MainScreen(
                onSearchButtonClick = {
                    navController.navigate(Screens.FinderScreen.route)
                },
                onSettingsButtonClick = {
                    navController.navigate(Screens.SettingsScreen.route)
                },
                onPlaylistsButtonClick = {
                    navController.navigate(Screens.PlaylistsScreen.route)
                },
                onFavouriteButtonClick = {
                    navController.navigate(Screens.FavoritesScreen.route)
                }
            )
        }

        composable(Screens.SettingsScreen.route) {
            SettingsScreen { navController.popBackStack() }
        }

        composable(Screens.FinderScreen.route) {
            FinderScreen(
                viewModel = searchViewModel,
                onArrowBackClicked = { navController.popBackStack() },
                onTrackClick = { track -> navigateToTrack(track) }
            )
        }

        composable(Screens.PlaylistsScreen.route) {
            PlaylistsScreen(
                modifier = Modifier,
                playlistsViewModel = playlistsViewModel,
                addNewPlaylist = {
                    navController.navigate(Screens.NewPlaylistScreen.route)
                },
                navigateToPlaylist = { playlistId ->
                    navController.navigate("playlist_screen/$playlistId")
                },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(Screens.NewPlaylistScreen.route) {
            NewPlaylistScreen(
                viewModel = playlistsViewModel,
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screens.FavoritesScreen.route) {
            FavoritesScreen(
                viewModel = playlistsViewModel,
                onBack = { navController.popBackStack() },
                onTrackClick = { track -> navigateToTrack(track) }
            )
        }

        composable(
            route = Screens.PlaylistScreen.route,
            arguments = listOf(
                navArgument("playlistId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
            val viewModel: PlaylistViewModel = viewModel(
                factory = PlaylistViewModel.getViewModelFactory(playlistId)
            )
            PlaylistScreen(
                modifier = Modifier,
                viewModel = viewModel,
                navigateToTrack = { track -> navigateToTrack(track) },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screens.TrackDetailsScreen.route,
            arguments = listOf(
                navArgument("trackName") { type = NavType.StringType },
                navArgument("artistName") { type = NavType.StringType },
                navArgument("trackTime") { type = NavType.StringType },
                navArgument("artworkUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val rawName = backStackEntry.arguments?.getString("trackName").orEmpty()
            val rawArtist = backStackEntry.arguments?.getString("artistName").orEmpty()
            val rawTime = backStackEntry.arguments?.getString("trackTime").orEmpty()
            val rawArt = backStackEntry.arguments?.getString("artworkUrl").orEmpty()
            val trackName = URLDecoder.decode(rawName, StandardCharsets.UTF_8.toString())
            val artistName = URLDecoder.decode(rawArtist, StandardCharsets.UTF_8.toString())
            val trackTime = URLDecoder.decode(rawTime, StandardCharsets.UTF_8.toString())
            val artworkUrl = if (rawArt == ARTWORK_NONE) {
                null
            } else {
                URLDecoder.decode(rawArt, StandardCharsets.UTF_8.toString())
            }

            TrackDetailsScreen(
                trackName = trackName,
                artistName = artistName,
                trackTime = trackTime,
                artworkUrl = artworkUrl,
                viewModel = playlistsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
