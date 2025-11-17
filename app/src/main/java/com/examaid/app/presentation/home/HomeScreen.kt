package com.examaid.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold // YENİ EKLENDİ
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar // YENİ EKLENDİ
import androidx.compose.material3.TopAppBarDefaults // YENİ EKLENDİ
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.examaid.app.R

@OptIn(ExperimentalMaterial3Api::class) // YENİ EKLENDİ
@Composable
fun HomeFeedScreen(
    onOpenUpload: () -> Unit,
    onOpenArchive: () -> Unit,
    onOpenSchedule: () -> Unit
) {
    // Surface(modifier = Modifier.fillMaxSize()... ) <-- ESKİ KOD SİLİNDİ

    // YENİ SCAFFOLD VE TOPAPPBAR EKLENDİ
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.nav_home), // "Ana Sayfa"
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { paddingValues -> // paddingValues, TopAppBar'ın boşluğudur
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // TopAppBar'ın boşluğunu uygula
        ) {
            val horizontalPadding = when {
                maxWidth < 360.dp -> 16.dp
                maxWidth < 600.dp -> 24.dp
                else -> 32.dp
            }
            val isCompactWidth = maxWidth < 440.dp
            val sectionSpacing = if (isCompactWidth) 20.dp else 24.dp

            val insights = listOf(
                HomeInsight(
                    icon = Icons.Outlined.Archive,
                    title = stringResource(id = R.string.home_insight_archive),
                    subtitle = stringResource(id = R.string.home_insight_archive_desc),
                    onClick = onOpenArchive
                ),
                HomeInsight(
                    icon = Icons.Outlined.CalendarMonth,
                    title = stringResource(id = R.string.home_insight_repeat),
                    subtitle = stringResource(id = R.string.home_insight_repeat_desc),
                    onClick = onOpenSchedule
                ),
                HomeInsight(
                    icon = Icons.Outlined.Lightbulb,
                    title = stringResource(id = R.string.home_insight_notes),
                    subtitle = stringResource(id = R.string.home_insight_notes_desc),
                    onClick = onOpenUpload
                )
            )

            val quickActions = listOf(
                HomeQuickAction(
                    icon = Icons.Outlined.CameraAlt,
                    title = stringResource(id = R.string.home_action_upload_title),
                    subtitle = stringResource(id = R.string.home_action_upload_desc),
                    onClick = onOpenUpload
                ),
                HomeQuickAction(
                    icon = Icons.Outlined.Archive,
                    title = stringResource(id = R.string.home_action_archive_title),
                    subtitle = stringResource(id = R.string.home_action_archive_desc),
                    onClick = onOpenArchive
                ),
                HomeQuickAction(
                    icon = Icons.Outlined.CalendarMonth,
                    title = stringResource(id = R.string.home_action_schedule_title),
                    subtitle = stringResource(id = R.string.home_action_schedule_desc),
                    onClick = onOpenSchedule
                )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    top = sectionSpacing,
                    bottom = 8.dp // Navigation bar üstündeki boşluğu minimize ettik
                ),
                verticalArrangement = Arrangement.spacedBy(sectionSpacing)
            ) {
                item {
                    WelcomeSection(
                        onOpenSchedule = onOpenSchedule,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    InsightsSection(
                        items = insights,
                        isCompact = isCompactWidth
                    )
                }
                item {
                    QuickActionsSection(
                        actions = quickActions,
                        isCompact = isCompactWidth
                    )
                }
                item {
                    StudyTipSection()
                }
            }
        }
    }
}

private data class HomeInsight(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)

private data class HomeQuickAction(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)

@Composable
private fun WelcomeSection(
    onOpenSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = stringResource(id = R.string.home_overview_greeting),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = stringResource(id = R.string.home_overview_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.88f)
                )
                AssistChip(
                    onClick = onOpenSchedule,
                    label = { Text(text = stringResource(id = R.string.home_overview_schedule_cta)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.12f),
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        leadingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun InsightsSection(
    items: List<HomeInsight>,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(text = stringResource(id = R.string.home_insights_title))

        if (isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items.forEach { insight ->
                    HomeInsightCard(
                        insight = insight,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            items.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowItems.forEach { insight ->
                        HomeInsightCard(
                            insight = insight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeInsightCard(
    insight: HomeInsight,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = insight.onClick,
        modifier = modifier.heightIn(min = 148.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = insight.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = insight.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = insight.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    actions: List<HomeQuickAction>,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(text = stringResource(id = R.string.home_quick_actions_title))

        if (isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                actions.forEach { action ->
                    HomeQuickActionCard(
                        action = action,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            actions.chunked(2).forEach { rowActions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowActions.forEach { action ->
                        HomeQuickActionCard(
                            action = action,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowActions.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeQuickActionCard(
    action: HomeQuickAction,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = action.onClick,
        modifier = modifier.heightIn(min = 132.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = action.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StudyTipSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader(text = stringResource(id = R.string.home_tip_title))

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Text(
                    text = stringResource(id = R.string.home_tip_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
    )
}