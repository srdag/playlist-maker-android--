package com.example.playlistmaker.ui.playlists

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.data.network.Track
import com.example.playlistmaker.ui.search.TrackListItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    modifier: Modifier = Modifier,
    playlistViewModel: PlaylistViewModel,
    index: Int,
    navigateToTrack: (Track) -> Unit,
    navigateBack: () -> Unit,
    onClick: (Int?) -> Unit = {}
) {
    val playlist by playlistViewModel.playlist.collectAsState(null)
    val current = playlist
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    if (showDeleteDialog && current != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("") }, // Пустой заголовок как на скрине
            text = {
                Text(
                    text = stringResource(
                        R.string.delete_playlist_confirmation,
                        current.name
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        playlistViewModel.deletePlaylist()
                        showDeleteDialog = false
                        navigateBack()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.yes).uppercase(),
                        color = Color(0xFF3772E7) // Синий цвет для "ДА"
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        text = stringResource(R.string.no).uppercase(),
                        color = Color(0xFF3772E7) // Синий цвет для "НЕТ"
                    )
                }
            }
        )
    }

    if (showBottomSheet && current != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                )
            }
        ) {
            PlaylistMenuBottomSheet(
                playlist = current,
                onShareClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showBottomSheet = false
                    }
                },
                onEditClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showBottomSheet = false
                    }
                },
                onDeleteClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showBottomSheet = false
                        showDeleteDialog = true
                    }
                }
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (current == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            // Стрелка назад поверх лоадера.
            IconButton(
                onClick = navigateBack,
                modifier = Modifier.padding(top = 24.dp, start = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    PlaylistHeader(
                        playlist = current,
                        onBack = navigateBack,
                        onMoreClick = { showBottomSheet = true }
                    )
                }
                items(current.tracks) { track ->
                    TrackListItem(track = track) {
                        navigateToTrack(track)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistHeader(
    playlist: com.example.playlistmaker.domain.model.Playlist,
    onBack: () -> Unit,
    onMoreClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Большая квадратная обложка во всю ширину экрана.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (!playlist.coverImageUri.isNullOrBlank()) {
                SubcomposeAsyncImage(
                    model = Uri.parse(playlist.coverImageUri),
                    contentDescription = playlist.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_image_placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Стрелка назад поверх обложки в левом верхнем углу.
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(top = 24.dp, start = 4.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Text(
                text = playlist.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (playlist.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = playlist.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = playlistMeta(playlist.tracks),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Kebab-меню.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMoreClick) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Считает общую длительность плейлиста (в минутах) и возвращает строку вида
 * "300 минут · 98 треков".
 */
@Composable
private fun playlistMeta(tracks: List<Track>): String {
    val totalSeconds = tracks.sumOf { track ->
        val parts = track.trackTime.split(":")
        if (parts.size == 2) {
            (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
        } else 0
    }
    val totalMinutes = totalSeconds / 60
    val minutesWord = stringResource(R.string.minutes_word)
    val tracksWord = stringResource(R.string.tracks_word)
    return "$totalMinutes $minutesWord · ${tracks.size} $tracksWord"
}

@Composable
private fun PlaylistMenuBottomSheet(
    playlist: com.example.playlistmaker.domain.model.Playlist,
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Шапка в BottomSheet: маленькая обложка + инфо.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (!playlist.coverImageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = Uri.parse(playlist.coverImageUri),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_image_placeholder),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = playlist.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${playlist.tracks.size} ${stringResource(R.string.tracks_word)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Пункты меню.
        BottomSheetMenuItem(
            text = stringResource(R.string.share_playlist),
            onClick = onShareClick
        )
        BottomSheetMenuItem(
            text = stringResource(R.string.edit_information),
            onClick = onEditClick
        )
        BottomSheetMenuItem(
            text = stringResource(R.string.delete_playlist),
            onClick = onDeleteClick
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun BottomSheetMenuItem(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
