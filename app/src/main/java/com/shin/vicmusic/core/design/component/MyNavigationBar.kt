package com.shin.vicmusic.core.design.component

import android.R.attr.contentDescription
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.Greeting
import com.shin.vicmusic.core.design.theme.LocalDividerColor
import com.shin.vicmusic.core.design.theme.SpaceExtraMedium
import com.shin.vicmusic.core.design.theme.SpaceSmallHeight
import com.shin.vicmusic.core.design.theme.VicMusicTheme
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
    Row(modifier = modifier.fillMaxWidth().background(LocalDividerColor.current)) {
        destinations.forEachIndexed { index, destination ->
            val selected = destination.route == currentDestination
            val color = if (selected)
                MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
            val interactionSource = remember { MutableInteractionSource() }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = SpaceExtraMedium)
                    //.navigationBarsPadding()
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource,
                    ){
                        onNavigateToDestination(index)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = if (selected) destination.selectedIcon else destination.unselectedIcon),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
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
