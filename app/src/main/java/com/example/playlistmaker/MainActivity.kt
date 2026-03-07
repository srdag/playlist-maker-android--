package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.playlistmaker.navigation.PlaylistHost
import com.example.playlistmaker.navigation.Screens

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            PlaylistHost(navController)
//            MainScreen(
//                onSearchButtonClick = {
//                    val searchIntent = Intent(this, FinderActivity::class.java)
//                    startActivity(searchIntent)
//                },
//                onSettingsButtonClick = {
//                    val settingsIntent = Intent(this, SettingsActivity::class.java)
//                    startActivity(settingsIntent)
//                },
//                onFavouriteButtonClick = {
//                    Toast.makeText(this, R.string.favourite, Toast.LENGTH_SHORT).show()
//                },
//                onPlaylistsButtonClick = {
//                    Toast.makeText(this, R.string.Playlists, Toast.LENGTH_SHORT).show()
//                }
//            )
        }
    }
}


@Composable
private fun ScreenButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Row {
            Icon(imageVector = icon, contentDescription = null, tint = Color.Black)
            Text(text = text, color = Color.Black)
        }

        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}


@Composable
fun MainScreen(
    onFavouriteButtonClick: () -> Unit = {},
    onPlaylistsButtonClick: () -> Unit = {},
    onSearchButtonClick: () -> Unit = {},
    onSettingsButtonClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .background(Color.Blue)
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.header),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp, start = 16.dp)
        )
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .fillMaxSize()
        ) {
            ScreenButton(stringResource(R.string.Search), Icons.Default.Search) {
                onSearchButtonClick()
            }

            ScreenButton(stringResource(R.string.Playlists), Icons.Default.DateRange) {
                onPlaylistsButtonClick()
            }

            ScreenButton(stringResource(R.string.favourite), Icons.Default.FavoriteBorder) {
                onFavouriteButtonClick()
            }

            ScreenButton(stringResource(R.string.settings), Icons.Default.Settings) {
                onSettingsButtonClick()
            }

        }

    }
}

