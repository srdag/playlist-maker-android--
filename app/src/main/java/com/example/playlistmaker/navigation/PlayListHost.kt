package com.example.playlistmaker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playlistmaker.FinderScreen
import com.example.playlistmaker.ui.activity.MainScreen
import com.example.playlistmaker.SettingsScreen

@Composable
fun PlaylistHost(
    navController: NavHostController,
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
            FinderScreen { navController.popBackStack() }
        }
    }
}
