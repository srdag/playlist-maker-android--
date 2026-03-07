package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController


class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            SettingsScreen()
        }
    }
}


@SuppressLint("QueryPermissionsNeeded")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onArrowBackClicked: () -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_text)) },
                navigationIcon = { IconButton(onClick = {
                    onArrowBackClicked()
                }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                ) {
                    Text(text = stringResource(R.string.black_theme_text), fontSize = 15.sp)
                    Switch(
                        checked = false,
                        onCheckedChange = {},
                        colors = SwitchDefaults.colors(
                            uncheckedBorderColor = Color.White,
                            uncheckedTrackColor = Color.White,
                            uncheckedThumbColor = Color.Gray,

                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                        .clickable {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "вступай в приложение")
                            }
                            context.startActivity(
                                Intent.createChooser(
                                    intent,
                                    "Выберите приложение для отправки"
                                )
                            )
                        }
                ) {
                    Text(text = stringResource(R.string.share_app), fontSize = 15.sp)
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Share, null, tint = Color.Gray)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                        .clickable {
                            val uri = R.string.mailto_dorogaada_yandex_ru.toString().toUri()
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
                ) {
                    Text(text = stringResource(R.string.write_to_developers), fontSize = 15.sp)
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Face,  null, tint = Color.Gray)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                ) {
                    Text(text = stringResource(R.string.artem), fontSize = 15.sp)
                    IconButton(onClick = {
                        val uri = R.string.https_praktikum_yandex_ru_offer.toString().toUri()

                        // Создаем Intent для открытия веб-страницы в браузере
                        val intent = Intent(Intent.ACTION_VIEW, uri)

                        // Проверяем, что хотя бы одно приложение поддерживает это действие
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        }
                    }) {
                        Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray)
                    }
                }
            }
        }
    }
}

