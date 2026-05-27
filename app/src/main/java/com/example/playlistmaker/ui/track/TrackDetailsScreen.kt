package com.example.playlistmaker.ui.track

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.data.network.Track
import com.example.playlistmaker.ui.playlists.PlaylistsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsScreen(
    trackName: String,
    artistName: String,
    trackTime: String,
    viewModel: PlaylistsViewModel,
    onBack: () -> Unit
) {
    // Восстанавливаем исходный трек по аргументам навигации.
    val initialTrack = remember(trackName, artistName, trackTime) {
        Track(
            id = (trackName + artistName).hashCode().toLong(),
            trackName = trackName,
            artistName = artistName,
            trackTime = trackTime
        )
    }

    // Текущее состояние трека (может быть обновлено из базы данных).
    var currentTrack by remember(initialTrack) { mutableStateOf(initialTrack) }
    val playlists by viewModel.playlists.collectAsState(emptyList())
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    // При входе на экран подтягиваем актуальные данные трека из базы
    // (например, если он уже был помечен как избранный).
    LaunchedEffect(initialTrack) {
        val dbTrack = viewModel.isExist(initialTrack)
        if (dbTrack != null) {
            currentTrack = dbTrack
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.track_details)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(140.dp),
                painter = painterResource(id = R.drawable.ic_music),
                contentDescription = currentTrack.trackName,
                colorFilter = ColorFilter.tint(Color.Gray)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = currentTrack.trackName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentTrack.artistName,
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.duration_label) + ": " + currentTrack.trackTime
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = {
                        val newFav = !currentTrack.favorite
                        val updated = currentTrack.copy(favorite = newFav)
                        currentTrack = updated
                        viewModel.toggleFavorite(updated, newFav)
                    }) {
                        Icon(
                            imageVector = if (currentTrack.favorite)
                                Icons.Filled.Favorite
                            else
                                Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(R.string.add_to_favorite),
                            tint = if (currentTrack.favorite) Color.Red else Color.Gray
                        )
                    }
                    Text(
                        text = stringResource(R.string.add_to_favorite),
                        fontSize = 12.sp
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { showSheet = true }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(R.string.add_to_playlist_action)
                        )
                    }
                    Text(
                        text = stringResource(R.string.add_to_playlist_action),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.select_playlist),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (playlists.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_playlists),
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn {
                        items(playlists.size) { idx ->
                            val p = playlists[idx]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val updated = currentTrack.copy(playlistId = p.id)
                                        currentTrack = updated
                                        viewModel.insertTrackToPlaylist(updated, p.id)
                                        coroutineScope.launch {
                                            sheetState.hide()
                                            showSheet = false
                                        }
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(end = 8.dp),
                                    painter = painterResource(id = R.drawable.ic_music),
                                    contentDescription = p.name,
                                    colorFilter = ColorFilter.tint(Color.Gray)
                                )
                                Column {
                                    Text(p.name, fontSize = 16.sp)
                                    Text(
                                        text = "${p.tracks.size} tracks",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            HorizontalDivider(thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}
