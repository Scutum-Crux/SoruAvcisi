package com.examaid.app.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.examaid.app.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit
) {
    val pulse = remember { Animatable(0.2f) }

    LaunchedEffect(Unit) {
        pulse.animateTo(
            targetValue = 0.6f,
            animationSpec = tween(durationMillis = 1_200, easing = LinearEasing)
        )
        delay(1_200)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.secondary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        val accent = MaterialTheme.colorScheme.secondary
        PulsingRing(progress = pulse.value, accentColor = accent)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.displaySmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bilgini güçlendir, tekrarını akıllandır.",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.8f)),
                modifier = Modifier.alpha(0.9f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(32.dp),
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
private fun PulsingRing(progress: Float, accentColor: Color) {
    Canvas(
        modifier = Modifier
            .size(160.dp)
            .alpha(0.85f)
    ) {
        val radius = size.minDimension / 2
        drawCircle(
            color = Color.White.copy(alpha = 0.2f),
            style = Stroke(width = 8f),
            radius = radius
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.45f),
            radius = radius * progress
        )

        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color.White, Color.White.copy(alpha = 0.2f)),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            ),
            radius = radius * 0.35f
        )

        drawCircle(
            color = accentColor,
            radius = radius * 0.22f
        )
    }
}

