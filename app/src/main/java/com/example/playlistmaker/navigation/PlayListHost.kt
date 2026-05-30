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
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.activity.MainScreen
import com.example.playlistmaker.ui.playlists.FavoritesScreen
import com.example.playlistmaker.ui.playlists.NewPlaylistScreen
import com.example.playlistmaker.ui.playlists.PlaylistScreen
import com.example.playlistmaker.ui.playlists.PlaylistViewModel
import com.example.playlistmaker.ui.playlists.PlaylistsScreen
import com.example.playlistmaker.ui.playlists.PlaylistsViewModel
import com.example.playlistmaker.ui.search.FinderScreen
import com.example.playlistmaker.ui.search.SearchViewModel
import com.example.playlistmaker.ui.theme.ThemeViewModel
import com.example.playlistmaker.ui.track.TrackDetailsScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets



private const val ARTWORK_NONE = "__none__"

@Composable
fun PlaylistHost(
    navController: NavHostController,
    searchViewModel: SearchViewModel,
    playlistsViewModel: PlaylistsViewModel,
    themeViewModel: ThemeViewModel,
) {


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
            SettingsScreen(
                themeViewModel = themeViewModel,
                onArrowBackClicked = { navController.popBackStack() }
            )
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
                navigateToPlaylist = { index ->
                    navController.navigate("playlist_screen/$index")
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
                navArgument("index") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: 0
            val viewModel: PlaylistViewModel = viewModel(
                factory = PlaylistViewModel.getViewModelFactory(index.toLong())
            )
            PlaylistScreen(
                modifier = Modifier,
                playlistViewModel = viewModel,
                index = index,
                navigateToTrack = { track -> navigateToTrack(track) },
                navigateBack = { navController.popBackStack() },
                onClick = {  }
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



