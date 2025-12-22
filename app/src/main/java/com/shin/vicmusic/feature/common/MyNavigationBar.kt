package com.shin.vicmusic.feature.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.theme.SpaceSmallHeight
import com.shin.vicmusic.feature.main.TopLevelDestination

/**
 * 底部导航
 */
@Composable
fun MyNavigationBar(
    destinations: List<TopLevelDestination>,
    currentDestination: String,
    onNavigateToDestination: (Int) -> Unit,
    modifier: Modifier = Modifier
): Unit {
    Row(modifier = modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.surface)) {
        destinations.forEachIndexed { index, destination ->
            val selected = destination.route == currentDestination
            val color = if (selected)
                MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
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
                // [修改] 使用 Icon 组件代替 Image，支持 ImageVector 和 Tint
                Icon(
                    imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                    contentDescription = stringResource(id = destination.titleTextId),
                    modifier = Modifier.size(25.dp),
                    tint = color // [关键] 直接设置图标颜色
                )
                SpaceSmallHeight()
                Text(
                    text = stringResource(id = destination.titleTextId),
                    color = color
                )
            }
        }
    }
}
