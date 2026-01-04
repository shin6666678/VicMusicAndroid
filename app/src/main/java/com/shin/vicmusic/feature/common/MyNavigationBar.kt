package com.shin.vicmusic.feature.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.theme.SpaceSmallHeight
import com.shin.vicmusic.feature.main.TopLevelDestination

/**
 * 底部导航 (支持动态红点)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyNavigationBar(
    destinations: List<TopLevelDestination>,
    currentDestination: String,
    onNavigateToDestination: (Int) -> Unit,
    badgeCounts: Map<TopLevelDestination, Int> = emptyMap(),
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.surface)) {
        destinations.forEachIndexed { index, destination ->
            val selected = destination.route == currentDestination
            val color = if (selected)
                MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface

            //获取当前 Tab 的未读数 (默认为 0)
            val badgeCount = badgeCounts[destination] ?: 0

            val interactionSource = remember { MutableInteractionSource() }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource,
                    ){
                        onNavigateToDestination(index)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ✅ 使用 BadgedBox 包裹 Icon
                BadgedBox(
                    badge = {
                        if (badgeCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = Color.White,
                                modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                            ) {
                                val displayCount = if (badgeCount > 99) "99+" else badgeCount.toString()
                                Text(text = displayCount)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = stringResource(id = destination.titleTextId),
                        modifier = Modifier.size(25.dp),
                        tint = color
                    )
                }

                SpaceSmallHeight()
                Text(
                    text = stringResource(id = destination.titleTextId),
                    color = color
                )
            }
        }
    }
}