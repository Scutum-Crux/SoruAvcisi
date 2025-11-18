package com.examaid.app.presentation.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.examaid.app.R

@Composable
fun SettingsScreen() {
    PlaceholderScreen(
        title = stringResource(id = R.string.nav_settings), // "Ayarlar"
        subtitle = stringResource(id = R.string.settings_placeholder_subtitle)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderScreen(title: String, subtitle: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0, 0, 0, 0) // <-- ÖNCEKİ HALİ
            )
        }
    ) { paddingValues -> // Bu, TopAppBar'ın kapladığı alanı içerir
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Bu padding, içeriğin TopAppBar'ın altında başlamasını sağlar
                .padding(paddingValues)
                .padding(
                    // Bu padding, metnin kenarlardan ve üstten
                    // ne kadar içeride başlayacağını belirler.
                    top = 16.dp, // BAŞLIK ALTINA 16.DP BOŞLUK VER
                    start = 32.dp,
                    end = 32.dp,
                    bottom = 16.dp
                ),

            // --- ÇÖZÜM TAM OLARAK BU SATIRDIR ---
            // Bu satır, metnin dikey olarak ortalanmasını engeller
            // ve onu ekranın en üstüne (padding'ler hariç) sabitler.
            verticalArrangement = Arrangement.Top,

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // "Kişisel tercihlerini..." metni
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}