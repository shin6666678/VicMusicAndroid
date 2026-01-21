package com.shin.vicmusic.feature.common.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shin.vicmusic.core.domain.Comment
import com.shin.vicmusic.core.domain.CommentThread
import com.shin.vicmusic.util.TimeUtil

/**
 * A composable that displays a full comment thread, including the root comment and its replies.
 */
@Composable
fun ItemCommentThread(
    thread: CommentThread,
    onLikeClick: (commentId: String, rootId: String?) -> Unit,
    onReplyClick: (rootComment: Comment, replyTo: Comment?) -> Unit,
    onViewMoreRepliesClick: (rootCommentId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 12.dp)) {
        // 1. Render the root comment
        CommentContent(
            comment = thread.rootComment,
            onLikeClick = { onLikeClick(thread.rootComment.id, null) },
            onReplyClick = { onReplyClick(thread.rootComment, null) }
        )

        // 2. Render the replies, if any
        if (thread.replies.isNotEmpty()) {
            Column(modifier = Modifier.padding(start = 52.dp)) { // Indent replies
                thread.replies.forEach { reply ->
                    CommentContent(
                        comment = reply,
                        onLikeClick = { onLikeClick(reply.id, thread.rootComment.id) },
                        onReplyClick = { onReplyClick(thread.rootComment, reply) },
                        isReply = true
                    )
                }

                // 3. "View More" button
                if (thread.hasMoreReplies) {
                    Text(
                        text = "查看全部 ${thread.totalReplyCount} 条回复 >",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable { onViewMoreRepliesClick(thread.rootComment.id) }
                    )
                }
            }
        }
    }
}

/**
 * A stateless composable that displays the content of a single comment.
 */
@Composable
private fun CommentContent(
    comment: Comment,
    onLikeClick: () -> Unit,
    onReplyClick: () -> Unit,
    isReply: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = comment.user.headImg,
            contentDescription = "${comment.user.name}'s avatar",
            modifier = Modifier
                .size(if (isReply) 28.dp else 40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = comment.user.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable(onClick = onReplyClick)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = TimeUtil.getFriendlyTimeSpan(comment.createTime),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Like button and count
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onLikeClick)
        ) {
            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = "Like",
                tint = if (comment.isLiked) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = comment.likeCount.toString(),
                color = if (comment.isLiked) MaterialTheme.colorScheme.primary else Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}
