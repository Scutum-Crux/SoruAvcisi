package com.examaid.app.presentation.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api // YENİ EKLENDİ
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold // YENİ EKLENDİ
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar // YENİ EKLENDİ
import androidx.compose.material3.TopAppBarDefaults // YENİ EKLENDİ
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.examaid.app.R

@Composable
fun ScheduleScreen() {
    PlaceholderScreen(
        title = stringResource(id = R.string.nav_schedule), // "Planlar" (strings.xml dosyasından)
        subtitle = stringResource(id = R.string.schedule_placeholder_subtitle)
    )
}

@Composable
fun SettingsScreen() {
    PlaceholderScreen(
        title = stringResource(id = R.string.nav_settings), // "Ayarlar" (strings.xml dosyasından)
        subtitle = stringResource(id = R.string.settings_placeholder_subtitle)
    )
}

@OptIn(ExperimentalMaterial3Api::class) // YENİ EKLENDİ
@Composable
private fun PlaceholderScreen(title: String, subtitle: String) {
    // Surface(...) <-- ESKİ KOD SİLİNDİ

    // YENİ SCAFFOLD VE TOPAPPBAR EKLENDİ
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title, // "Planlar" veya "Ayarlar"
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = TopAppBarDefaults.windowInsets // DİNAMİK ÜST BOŞLUK
            )
        }
    ) { paddingValues -> // paddingValues, TopAppBar'ın boşluğudur
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // TopAppBar'ın boşluğunu uygula
                .padding(
                    top = 16.dp, // İçeriğin üstten boşluğu
                    start = 32.dp,
                    end = 32.dp,
                    bottom = 16.dp // <-- DEĞİŞİKLİK BURADA (O devasa beyaz boşluğu sildik)
                ),
            verticalArrangement = Arrangement.Top, // İçeriği en üste hizala
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Text(text = title, ...) <-- BU SATIR ARTIK TopAppBar'DA
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}