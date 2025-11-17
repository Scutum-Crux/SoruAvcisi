package com.examaid.app.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.examaid.app.R
import com.examaid.app.core.navigation.Screen
import com.examaid.app.presentation.home.HomeFeedScreen
import com.examaid.app.presentation.upload.PhotoArchiveScreen
import com.examaid.app.presentation.upload.UploadPhotoScreen
import com.examaid.app.presentation.util.ScheduleScreen
import com.examaid.app.presentation.util.SettingsScreen

private data class BottomDestination(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val navController = rememberNavController()
    val destinations = listOf(
        BottomDestination(Screen.HomeFeed.route, R.string.nav_home, Icons.Outlined.Home),
        BottomDestination(Screen.PhotoArchive.route, R.string.nav_archive, Icons.Outlined.Archive),
        BottomDestination(Screen.UploadPhoto.route, R.string.nav_upload, Icons.Outlined.CameraAlt),
        BottomDestination(Screen.Schedule.route, R.string.nav_schedule, Icons.Outlined.CalendarMonth),
        BottomDestination(Screen.Settings.route, R.string.nav_settings, Icons.Outlined.Settings)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            DashboardBottomBar(
                destinations = destinations,
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.HomeFeed.route,
            modifier = Modifier
                .padding(innerPadding) // SADECE BU KALSIN. BU, İÇERİĞİN "MAVİ" MENÜNÜN ARKASINA GİTMESİNİ ENGELLER.
        ) {
            composable(Screen.HomeFeed.route) {
                HomeFeedScreen(
                    onOpenUpload = { navController.navigate(Screen.UploadPhoto.route) },
                    onOpenArchive = { navController.navigate(Screen.PhotoArchive.route) },
                    onOpenSchedule = { navController.navigate(Screen.Schedule.route) }
                )
            }

            composable(Screen.PhotoArchive.route) {
                PhotoArchiveScreen(
                    onBack = { navController.popBackStack() },
                    showBack = false
                )
            }

            composable(Screen.Schedule.route) {
                ScheduleScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
            }

            composable(Screen.UploadPhoto.route) {
                UploadPhotoScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = {
                        navController.navigate(Screen.PhotoArchive.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardBottomBar(
    destinations: List<BottomDestination>,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val horizontalPadding = if (configuration.screenWidthDp < 380) 12.dp else 20.dp
    val verticalPadding = if (configuration.screenHeightDp < 700) 10.dp else 14.dp
    val barHeight = if (configuration.screenHeightDp < 700) 56.dp else 64.dp

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .windowInsetsPadding(WindowInsets.navigationBars), // Bu, 3-düğmeli çubuk için DİNAMİK boşluk sağlar. "Beyazlık" değil.
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .height(barHeight)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            destinations.forEach { destination ->
                BottomBarItem(
                    destination = destination,
                    isSelected = currentRoute == destination.route,
                    onClick = {
                        if (currentRoute != destination.route) {
                            onNavigate(destination.route)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomBarItem(
    destination: BottomDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val highlightColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) else Color.Transparent
    val iconColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = highlightColor,
        tonalElevation = if (isSelected) 2.dp else 0.dp,
        modifier = Modifier.size(48.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = destination.icon,
                contentDescription = stringResource(id = destination.labelRes),
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}