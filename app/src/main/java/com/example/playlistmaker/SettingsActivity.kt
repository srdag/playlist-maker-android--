package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@SuppressLint("QueryPermissionsNeeded")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onArrowBackClicked: () -> Unit
) {

    val context = LocalContext.current

    val emailUri = stringResource(R.string.mailto_dorogaada_yandex_ru)
    val agreementUrl = stringResource(R.string.https_praktikum_yandex_ru_offer)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_text)) },
                navigationIcon = {
                    IconButton(onClick = onArrowBackClicked) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(stringResource(R.string.black_theme_text))

                Switch(
                    checked = false,
                    onCheckedChange = {}
                )
            }

            SettingsItem(
                text = stringResource(R.string.share_app),
                icon = Icons.Default.Share
            ) {

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "вступай в приложение")
                }

                context.startActivity(
                    Intent.createChooser(intent, null)
                )
            }

            SettingsItem(
                text = stringResource(R.string.write_to_developers),
                icon = Icons.Default.Face
            ) {

                val uri = Uri.parse(emailUri)
                    .buildUpon()
                    .appendQueryParameter(
                        "subject",
                        "Сообщение разработчикам и разработчицам приложения Playlist Maker"
                    )
                    .appendQueryParameter(
                        "body",
                        "Спасибо разработчикам и разработчицам за крутое приложение!"
                    )
                    .build()

                val intent = Intent(Intent.ACTION_SENDTO, uri)

                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }
            }

            SettingsItem(
                text = stringResource(R.string.artem),
                icon = Icons.Default.KeyboardArrowRight
            ) {

                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(agreementUrl)
                )

                context.startActivity(intent)
            }
        }
    }
}

@Composable
private fun SettingsItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(text)

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}