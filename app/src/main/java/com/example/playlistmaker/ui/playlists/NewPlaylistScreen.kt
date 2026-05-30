package com.example.playlistmaker.ui.playlists

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.theme.BrandBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPlaylistScreen(
    viewModel: PlaylistsViewModel,
    onSaved: () -> Unit,
    onBack: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val coverImageUri by viewModel.coverImageUri.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setCoverImageUri(it.toString()) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        }
    }

    fun launchPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            imagePickerLauncher.launch("image/*")
        } else {
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                imagePickerLauncher.launch("image/*")
            } else {
                permissionLauncher.launch(permission)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 24.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = stringResource(R.string.new_playlist),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { launchPicker() },
                    contentAlignment = Alignment.Center
                ) {
                    if (coverImageUri != null) {
                        AsyncImage(
                            model = Uri.parse(coverImageUri),
                            contentDescription = stringResource(R.string.playlist_cover),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_image_placeholder),
                            contentDescription = stringResource(R.string.playlist_cover),
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name_required)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlue,
                        focusedLabelColor = BrandBlue,
                        cursorColor = BrandBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description_optional)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandBlue,
                        focusedLabelColor = BrandBlue,
                        cursorColor = BrandBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.createNewPlayList(context, name.trim(), description.trim())
                        onSaved()
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandBlue,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(stringResource(R.string.create), fontSize = 14.sp)
            }
        }
    }
}



