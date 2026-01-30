package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}



@Composable
private fun ScreenButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(Color.White)) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Icon(imageVector = icon, contentDescription = null, tint = Color.Black)
                Text(text = text, color = Color.Black)
            }

            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)

        }

    }
}


@Preview
@Composable
fun MainScreen(context: Context = LocalContext.current) {
    Column(
        modifier = Modifier.background(Color.Blue).fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.header),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f).padding(top = 30.dp, start = 10.dp)
        )

        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(10))
                .fillMaxSize()
                .weight(7f)
        ) {
            ScreenButton("Поиск", Icons.Default.Search) {
                Toast.makeText(context, "Поиск", Toast.LENGTH_SHORT).show()
            }

            ScreenButton("Плейлисты", Icons.Default.DateRange) {
                Toast.makeText(context, "Плейлисты", Toast.LENGTH_SHORT).show()
            }

            ScreenButton("Избранное", Icons.Default.FavoriteBorder) {
                Toast.makeText(context, "Избранное", Toast.LENGTH_SHORT).show()
            }

            ScreenButton("Настройки", Icons.Default.Settings) {
                Toast.makeText(context, "Настройки", Toast.LENGTH_SHORT).show()
            }

        }

    }
}

