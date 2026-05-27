package com.example.playlistmaker.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinderScreen(
    viewModel: SearchViewModel,
    onArrowBackClicked: () -> Unit,
    onTrackClick: (Track) -> Unit = {}
) {
    val screenState by viewModel.searchScreenState.collectAsState()
    var text by remember { mutableStateOf("") }

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
                .fillMaxWidth(),
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
                        modifier = Modifier.clickable {
                            if (text.isNotBlank()) viewModel.search(text)
                        },
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Icon"
                    )
                },
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(onClick = {
                            text = ""
                            viewModel.clearSearch()
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            when (screenState) {
                is SearchState.Initial -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.enter_search_string))
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
                    val tracks = (screenState as SearchState.Success).list
                    if (tracks.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.nothing_found))
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(tracks.size) { index ->
                                TrackListItem(track = tracks[index]) {
                                    onTrackClick(tracks[index])
                                }
                                HorizontalDivider(thickness = 0.5.dp)
                            }
                        }
                    }
                }

                is SearchState.Fail -> {
                    val error = (screenState as SearchState.Fail).error
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Ошибка: $error", color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun TrackListItem(track: Track, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            modifier = Modifier
                .size(48.dp)
                .padding(end = 8.dp),
            painter = painterResource(id = R.drawable.ic_music),
            contentDescription = "Трек ${track.trackName}",
            colorFilter = ColorFilter.tint(Color.Gray)
        )
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(track.trackName, fontWeight = FontWeight.Bold)
            Text(track.artistName, fontSize = 12.sp, color = Color.Gray)
        }
        Text(
            text = track.trackTime,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
