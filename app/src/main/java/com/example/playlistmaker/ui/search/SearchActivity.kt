package com.example.playlistmaker.ui.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.data.network.Track

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinderScreen(
    viewModel: SearchViewModel,
    onArrowBackClicked: () -> Unit,
    onTrackClick: (Track) -> Unit = {},
) {
    val screenState by viewModel.searchScreenState.collectAsState()
    val history by viewModel.historyState.collectAsState()
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Шапка: стрелка назад + заголовок «Поиск».
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 24.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onArrowBackClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = stringResource(R.string.Search),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            // Скруглённое filled-поле поиска как на макете.
            TextField(
                value = text,
                onValueChange = {
                    text = it
                    if (it.isNotBlank()) {
                        viewModel.search(it)
                    } else {
                        viewModel.clearSearch()
                    }
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.Search),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.search_icon),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                text = ""
                                viewModel.clearSearch()
                                keyboardController?.hide()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear_search),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.size(8.dp))

            when (val state = screenState) {
                is SearchState.Initial -> {
                    if (history.isNotEmpty()) {
                        HistoryList(
                            history = history,
                            onHistoryItemClick = { query ->
                                text = query
                                viewModel.search(query)
                            },
                            onClearHistory = { viewModel.clearHistory() }
                        )
                    } else {
                        // На пустом дефолтном экране на макете ничего нет — оставляем чисто.
                    }
                }

                is SearchState.Searching -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is SearchState.Success -> {
                    if (state.list.isEmpty()) {
                        NothingFoundPlaceholder()
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.list) { track ->
                                TrackListItem(track = track) {
                                    viewModel.saveQueryToHistory(text)
                                    onTrackClick(track)
                                }
                            }
                        }
                    }
                }

                is SearchState.Fail -> {
                    ServerErrorPlaceholder(onRefresh = { viewModel.retry() })
                }
            }
        }
    }
}

@Composable
private fun HistoryList(
    history: List<String>,
    onHistoryItemClick: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(history) { query ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onHistoryItemClick(query) }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_history),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = query,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    )
                }
            }
        }
        OutlinedButton(
            onClick = onClearHistory,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(stringResource(R.string.clear_history))
        }
    }
}

@Composable
private fun NothingFoundPlaceholder() {
    Placeholder(
        iconRes = R.drawable.ic_nothing_found,
        message = stringResource(R.string.nothing_found),
        subtitle = null,
        actionText = null,
        onAction = null
    )
}

@Composable
private fun ServerErrorPlaceholder(onRefresh: () -> Unit) {
    Placeholder(
        iconRes = R.drawable.ic_no_internet,
        message = stringResource(R.string.server_error_title),
        subtitle = stringResource(R.string.server_error_subtitle),
        actionText = stringResource(R.string.refresh),
        onAction = onRefresh
    )
}

@Composable
private fun Placeholder(
    iconRes: Int,
    message: String,
    subtitle: String?,
    actionText: String?,
    onAction: (() -> Unit)?
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                modifier = Modifier.size(120.dp),
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color.Unspecified // используем оригинальные цвета внутри SVG
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = message,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            if (actionText != null && onAction != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(actionText)
                }
            }
        }
    }
}

/**
 * Элемент списка треков: маленькая обложка слева, название и "artist · time" в две строки,
 * стрелочка-chevron справа. Соответствует макетам.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackListItem(
    track: Track,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TrackArtwork(
            artworkUrl = track.artworkUrl,
            contentDescription = track.trackName,
            size = 44
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.trackName,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = "${track.artistName}  •  ${track.trackTime}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Универсальный компонент обложки. Скруглённая, с плейсхолдером-нотой
 * пока картинка не загружена / отсутствует.
 */
@Composable
fun TrackArtwork(
    artworkUrl: String?,
    contentDescription: String?,
    size: Int,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(4.dp)
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (artworkUrl.isNullOrBlank()) {
            ArtworkPlaceholder(size = size)
        } else {
            SubcomposeAsyncImage(
                model = artworkUrl,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                loading = { ArtworkPlaceholder(size = size) },
                error = { ArtworkPlaceholder(size = size) },
                modifier = Modifier.size(size.dp)
            )
        }
    }
}

@Composable
private fun ArtworkPlaceholder(size: Int) {
    Box(
        modifier = Modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size((size * 0.55f).dp),
            painter = painterResource(id = R.drawable.ic_music),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
