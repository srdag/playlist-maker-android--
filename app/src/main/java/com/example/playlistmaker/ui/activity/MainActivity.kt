package com.example.playlistmaker.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.navigation.PlaylistHost
import com.example.playlistmaker.ui.playlists.PlaylistsViewModel
import com.example.playlistmaker.ui.search.SearchViewModel
import com.example.playlistmaker.ui.theme.BrandBlue
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme
import com.example.playlistmaker.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    private val searchViewModel by viewModels<SearchViewModel> {
        SearchViewModel.getViewModelFactory()
    }
    private val playlistsViewModel by viewModels<PlaylistsViewModel> {
        PlaylistsViewModel.getViewModelFactory()
    }


    private val themeViewModel by viewModels<ThemeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkTheme by themeViewModel.darkTheme.collectAsStateWithLifecycle()
            PlaylistMakerTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                PlaylistHost(
                    navController = navController,
                    searchViewModel = searchViewModel,
                    playlistsViewModel = playlistsViewModel,
                    themeViewModel = themeViewModel
                )
            }
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
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MainScreen(
    onFavouriteButtonClick: () -> Unit = {},
    onPlaylistsButtonClick: () -> Unit = {},
    onSearchButtonClick: () -> Unit = {},
    onSettingsButtonClick: () -> Unit = {},
) {


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandBlue)
                    .padding(start = 20.dp, top = 56.dp, bottom = 24.dp, end = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.header),
                    color = androidx.compose.ui.graphics.Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                ScreenButton(
                    text = stringResource(R.string.Search),
                    icon = Icons.Default.Search,
                    onClick = onSearchButtonClick
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                ScreenButton(
                    text = stringResource(R.string.Playlists),
                    icon = ImageVector.vectorResource(id = R.drawable.playlist),
                    onClick = onPlaylistsButtonClick
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                ScreenButton(
                    text = stringResource(R.string.favourite),
                    icon = Icons.Default.FavoriteBorder,
                    onClick = onFavouriteButtonClick
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                ScreenButton(
                    text = stringResource(R.string.settings),
                    icon = Icons.Default.Settings,
                    onClick = onSettingsButtonClick
                )
            }
        }
    }
}



