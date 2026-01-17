// app/src/main/java/com/shin/vicmusic/feature/comment/component/CommentSection.kt

package com.shin.vicmusic.feature.comment.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.feature.comment.CommentUiState
import com.shin.vicmusic.feature.common.item.ItemComment

@Composable
fun CommentSection(
    uiState: CommentUiState,
    onLikeClick: (String) -> Unit,
    onAddComment: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    val comments = uiState.comments

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("评论区 (${comments.size})", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // 简易评论输入框
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("留下你的精彩评论吧") },
            trailingIcon = {
                TextButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            onAddComment(commentText)
                            commentText = "" // 清空输入框
                        }
                    }
                ) {
                    Text("发送")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading && comments.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null && comments.isEmpty()) {
            Text("加载评论失败: ${uiState.error}", color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(comments) { comment ->
                    ItemComment(comment = comment, onLikeClick = onLikeClick)
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                }

                // 加载更多
                if (uiState.hasMore) {
                    item {
                        LaunchedEffect(Unit) { // 当这个 item 可见时，触发加载更多
                            onLoadMore()
                        }
                        if (uiState.isLoading) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}
