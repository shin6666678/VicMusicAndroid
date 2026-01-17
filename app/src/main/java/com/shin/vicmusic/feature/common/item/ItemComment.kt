package com.shin.vicmusic.feature.common.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shin.vicmusic.core.domain.Comment

@Composable
fun ItemComment(
    comment: Comment,
    onLikeClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        AsyncImage(
            model = comment.userHeadImg,
            contentDescription = "用户头像",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = comment.userName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            // 你可以在这里添加时间显示，需要一个格式化函数
            // Text(text = formatTimestamp(comment.createTime), ...)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onLikeClick(comment.id) }
        ) {
            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = "点赞",
                tint = if (comment.liked) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = comment.likeCount.toString(),
                color = if (comment.liked) MaterialTheme.colorScheme.primary else Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}
