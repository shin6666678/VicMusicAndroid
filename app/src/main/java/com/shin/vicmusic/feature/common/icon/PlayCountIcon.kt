package com.shin.vicmusic.feature.common.icon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.theme.LocalAppColors

@Composable
fun PlayCountIcon(
    playCount: Int,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(colors.accentSecondary.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Headphones,
            contentDescription = "播放量",
            tint = colors.accentSecondary,
            modifier = Modifier.size(12.dp)
        )

        Text(
            text = "$playCount",
            style = MaterialTheme.typography.labelSmall,
            color = colors.accentSecondary
        )
    }
}