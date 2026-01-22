package com.shin.vicmusic.feature.feed.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun FeedItemFooter(
    likeCount: Int,
    commentCount: Int,
    modifier: Modifier = Modifier,
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround, // 保持均勻分佈
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Like Button (自定義組件，不再使用 IconButton)
        FeedActionButton(
            icon = Icons.Default.FavoriteBorder,
            text = likeCount.toString(),
            onClick = onLikeClick
        )

        // Comment Button
        FeedActionButton(
            icon = Icons.Outlined.ChatBubbleOutline,
            text = commentCount.toString(),
            onClick = onCommentClick
        )
    }
}

@Composable
private fun FeedActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 24.dp),
                onClick = onClick
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1
        )
    }
}