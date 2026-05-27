package com.example.playlistmaker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.example.playlistmaker.ui.playlists.PlaylistsScreen
import com.example.playlistmaker.ui.playlists.PlaylistsViewModel
import com.example.playlistmaker.ui.search.FinderScreen
import com.example.playlistmaker.ui.search.SearchViewModel
import com.example.playlistmaker.ui.track.TrackDetailsScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun PlaylistHost(
    navController: NavHostController,
    searchViewModel: SearchViewModel,
    playlistsViewModel: PlaylistsViewModel,
) {
    // Хелпер для безопасного формирования маршрута в Track details:
    // имена треков и артистов могут содержать пробелы и кириллицу.
    fun navigateToTrack(track: Track) {
        val name = URLEncoder.encode(track.trackName, StandardCharsets.UTF_8.toString())
        val artist = URLEncoder.encode(track.artistName, StandardCharsets.UTF_8.toString())
        val time = URLEncoder.encode(track.trackTime, StandardCharsets.UTF_8.toString())
        navController.navigate("track_details_screen/$name/$artist/$time")
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
                navigateToPlaylist = { _ ->
                    // Деталей плейлиста пока нет — можно расширить позже.
                    // Сейчас просто оставим без перехода, чтобы клик не падал.
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
            route = Screens.TrackDetailsScreen.route,
            arguments = listOf(
                navArgument("trackName") { type = NavType.StringType },
                navArgument("artistName") { type = NavType.StringType },
                navArgument("trackTime") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val rawName = backStackEntry.arguments?.getString("trackName").orEmpty()
            val rawArtist = backStackEntry.arguments?.getString("artistName").orEmpty()
            val rawTime = backStackEntry.arguments?.getString("trackTime").orEmpty()
            val trackName = URLDecoder.decode(rawName, StandardCharsets.UTF_8.toString())
            val artistName = URLDecoder.decode(rawArtist, StandardCharsets.UTF_8.toString())
            val trackTime = URLDecoder.decode(rawTime, StandardCharsets.UTF_8.toString())

            TrackDetailsScreen(
                trackName = trackName,
                artistName = artistName,
                trackTime = trackTime,
                viewModel = playlistsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
