package com.example.playlistmaker.ui.search

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.Search)) },
                navigationIcon = {
                    IconButton(onClick = onArrowBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    if (it.isNotBlank()) {
                        viewModel.search(it)
                    } else {
                        viewModel.clearSearch()
                    }
                },
                placeholder = { Text(stringResource(R.string.Search)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.search_icon)
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
                                contentDescription = stringResource(R.string.clear_search)
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            when (val state = screenState) {
                is SearchState.Initial -> {
                    // Когда строка поиска пуста — показываем историю запросов (если она есть).
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
                        EmptyHint(message = stringResource(R.string.enter_search_string))
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
                        Placeholder(
                            iconRes = R.drawable.ic_music,
                            message = stringResource(R.string.nothing_found)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.list) { track ->
                                TrackListItem(track = track) {
                                    // Сохраняем запрос в историю при выборе результата.
                                    viewModel.saveQueryToHistory(text)
                                    onTrackClick(track)
                                }
                                HorizontalDivider(thickness = 0.5.dp)
                            }
                        }
                    }
                }

                is SearchState.Fail -> {
                    Placeholder(
                        iconRes = R.drawable.ic_music,
                        message = stringResource(R.string.server_error),
                        actionText = stringResource(R.string.refresh),
                        onAction = { viewModel.retry() }
                    )
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
        Text(
            text = stringResource(R.string.search_history_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(history) { query ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onHistoryItemClick(query) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(text = query)
                }
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
        OutlinedButton(
            onClick = onClearHistory,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.clear_history))
        }
    }
}

@Composable
private fun EmptyHint(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(message, color = Color.Gray)
    }
}

@Composable
private fun Placeholder(
    iconRes: Int,
    message: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(96.dp),
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color.LightGray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            if ((actionText != null) && (onAction != null)) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onAction) {
                    Text(actionText)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackListItem(
    track: Track,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TrackArtwork(
            artworkUrl = track.artworkUrl,
            contentDescription = "Обложка ${track.trackName}",
            size = 48
        )
        Spacer(modifier = Modifier.size(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(track.trackName, fontWeight = FontWeight.Bold)
            Text(track.artistName, fontSize = 12.sp, color = Color.Gray)
        }
        Text(
            text = track.trackTime,
            modifier = Modifier.padding(start = 8.dp),
            fontSize = 12.sp,
            color = Color.Gray
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * Универсальный компонент обложки.
 * Пока картинка не загружена — рисует плейсхолдер (нота).
 * Если URL отсутствует или загрузка упала — также показывает плейсхолдер.
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
            .clip(shape),
        contentAlignment = Alignment.Center
    ) {
        if (artworkUrl.isNullOrBlank()) {
            ArtworkPlaceholder(size = size)
        } else {
            SubcomposeAsyncImage(
                model = artworkUrl,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                loading = { _ -> ArtworkPlaceholder(size = size) },
                error = { _ -> ArtworkPlaceholder(size = size) },
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
            modifier = Modifier.size((size * 0.6f).dp),
            painter = painterResource(id = R.drawable.ic_music),
            contentDescription = null,
            tint = Color.Gray
        )
    }
}
