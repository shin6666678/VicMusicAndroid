package com.shin.vicmusic.feature.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.feature.common.common.VicBadgedIcon
import com.shin.vicmusic.feature.main.TopLevelDestination

/**
 * 底部导航
 */
@Composable
fun MyNavigationBar(
    destinations: List<TopLevelDestination>,
    currentDestination: String,
    onNavigateToDestination: (Int) -> Unit,
    badgeCounts: Map<TopLevelDestination, Int> = emptyMap(),
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface
) {
    NavigationBar(
        modifier = modifier,
        containerColor = containerColor,
        windowInsets = WindowInsets(0.dp),
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

@Preview(name = "MyNavigationBar - 无背景")
@Composable
private fun MyNavigationBarTransparentPreview() {
    VicMusicTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.BottomCenter
        ) {
            MyNavigationBar(
                destinations = TopLevelDestination.entries,
                currentDestination = "呢",
                onNavigateToDestination = {},
                badgeCounts = mapOf(TopLevelDestination.ME to 3),
                containerColor = Color.Transparent
            )
        }
    }
}

@Preview(name = "MyNavigationBar - 默认背景")
@Composable
private fun MyNavigationBarDefaultPreview() {
    VicMusicTheme {
        MyNavigationBar(
            destinations = TopLevelDestination.entries,
            currentDestination = "",
            onNavigateToDestination = {},
            badgeCounts = mapOf(TopLevelDestination.ME to 3),
        )
    }
}
