package com.example.playlistmaker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playlistmaker.ui.search.FinderScreen
import com.example.playlistmaker.ui.activity.MainScreen
import com.example.playlistmaker.SettingsScreen
import com.example.playlistmaker.ui.search.SearchViewModel


@Composable
fun PlaylistHost(
    navController: NavHostController,
    viewModel: SearchViewModel,
) {
    NavHost(navController, Screens.MainScreen.route) {
        composable(Screens.MainScreen.route) {
            MainScreen(
                onSearchButtonClick = {
                    navController.navigate(Screens.FinderScreen.route)
                },
                onSettingsButtonClick = {
                    navController.navigate(Screens.SettingsScreen.route)
                }
            )
        }

        composable(Screens.SettingsScreen.route) {
            SettingsScreen { navController.popBackStack() }
        }

        composable(Screens.FinderScreen.route) {
            FinderScreen(viewModel = viewModel) { navController.popBackStack()  }
        }
    }
}
