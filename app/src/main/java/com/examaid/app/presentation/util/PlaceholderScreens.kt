package com.examaid.app.presentation.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
        title = stringResource(id = R.string.schedule_placeholder_title),
        subtitle = stringResource(id = R.string.schedule_placeholder_subtitle)
    )
}

@Composable
fun SettingsScreen() {
    PlaceholderScreen(
        title = stringResource(id = R.string.settings_placeholder_title),
        subtitle = stringResource(id = R.string.settings_placeholder_subtitle)
    )
}

@Composable
private fun PlaceholderScreen(title: String, subtitle: String) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

