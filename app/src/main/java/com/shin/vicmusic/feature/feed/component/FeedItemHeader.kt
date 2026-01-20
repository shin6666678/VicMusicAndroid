package com.shin.vicmusic.feature.feed.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.User
import com.shin.vicmusic.util.TimeUtil

@Composable
fun FeedItemHeader(
    author: Any, // Can be User or Artist
    timestamp: Long,
    actionText: String, // e.g., "分享了歌曲", "发布了新专辑"
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageUrl: String
    val name: String

    when (author) {
        is User -> {
            imageUrl = author.headImg
            name = author.name
        }
        is Artist -> {
            imageUrl = author.image
            name = author.name
        }
        else -> return // Or show a placeholder
    }

    Row(
        modifier = modifier.clickable(onClick = onProfileClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "$name's profile picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = TimeUtil.getFriendlyTimeSpan(timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
