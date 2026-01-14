package com.shin.vicmusic.feature.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.shin.vicmusic.feature.common.common.VicBadgedIcon
import com.shin.vicmusic.feature.main.TopLevelDestination

/**
 * 底部导航 (支持动态红点)
 */
@Composable
fun MyNavigationBar(
    destinations: List<TopLevelDestination>,
    currentDestination: String,
    onNavigateToDestination: (Int) -> Unit,
    badgeCounts: Map<TopLevelDestination, Int> = emptyMap(),
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        destinations.forEachIndexed { index, destination ->
            val selected = destination.route == currentDestination
            val badgeCount = badgeCounts[destination] ?: 0

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(index) },
                icon = {
                    VicBadgedIcon(
                        icon = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = stringResource(id = destination.titleTextId),
                        badgeCount = badgeCount
                    )
                },
                label = {
                    Text(text = stringResource(id = destination.titleTextId))
                },
                alwaysShowLabel = true
            )
        }
    }
}