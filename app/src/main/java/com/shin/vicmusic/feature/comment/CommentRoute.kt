package com.shin.vicmusic.feature.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.shin.vicmusic.feature.myInfo.navigateToMyInfo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Comment
import com.shin.vicmusic.core.domain.CommentThread
import com.shin.vicmusic.core.domain.User
import com.shin.vicmusic.feature.common.item.ItemCommentThread

@Composable
fun CommentRoute(
    resourceId: String,
    resourceType: String,
    onBackClick: () -> Unit,
    viewModel: CommentViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(resourceId, resourceType) {
        viewModel.loadComments(
            resourceType = resourceType,
            resourceId = resourceId,
            isRefresh = true
        )
    }

    CommentScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onLikeClick = viewModel::toggleLike,
        onAddComment = { content, parentId ->
            viewModel.addComment(resourceType, resourceId, content, parentId)
        },
        onLoadMore = {
            viewModel.loadComments(resourceType, resourceId)
        },
        onViewMoreRepliesClick = { rootCommentId ->
            viewModel.loadMoreReplies(rootCommentId)
        },
        onProfileClick = { userId -> navController.navigateToMyInfo(userId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    uiState: CommentUiState,
    onBackClick: () -> Unit,
    onLikeClick: (commentId: String, rootId: String?) -> Unit,
    onAddComment: (content: String, parentId: String?) -> Unit,
    onLoadMore: () -> Unit,
    onViewMoreRepliesClick: (rootCommentId: String) -> Unit,
    onProfileClick: (userId: String) -> Unit = {}
) {
    var commentText by remember { mutableStateOf("") }
    var replyInfo by remember { mutableStateOf<Pair<Comment, Comment?>?>(null) }

    val placeholderText = if (replyInfo?.second != null) {
        "回复 @${replyInfo?.second?.user?.name}:"
    } else if (replyInfo?.first != null) {
        "回复 @${replyInfo?.first?.user?.name}:"
    } else {
        "留下你的精彩评论吧"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("评论区 (${uiState.comments.size})") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                placeholder = { Text(placeholderText) },
                trailingIcon = {
                    TextButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                val parentId = replyInfo?.second?.id ?: replyInfo?.first?.id
                                onAddComment(commentText, parentId)
                                commentText = ""
                                replyInfo = null // Reset reply state
                            }
                        }
                    ) {
                        Text("发送")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            if (uiState.isLoading && uiState.comments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null && uiState.comments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("加载评论失败: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(uiState.comments, key = { it.rootComment.id }) { thread ->
                        ItemCommentThread(
                            thread = thread,
                            onLikeClick = onLikeClick,
                            onReplyClick = { root, reply -> replyInfo = Pair(root, reply) },
                            onViewMoreRepliesClick = onViewMoreRepliesClick,
                            onProfileClick = onProfileClick
                        )
                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    }

                    if (uiState.hasMore) {
                        item {
                            LaunchedEffect(Unit) { onLoadMore() }
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
}

@Preview(showBackground = true)
@Composable
fun CommentScreenPreview() {
    val mockUser1 = User(id = "user1", name = "Shin", headImg = "")
    val mockUser2 = User(id = "user2", name = "音乐爱好者", headImg = "")

    val rootComment = Comment(
        id = "1", user = mockUser1, content = "这首歌太好听了！",
        createTime = System.currentTimeMillis(), likeCount = 102, isLiked = true, replyCount = 0
    )

    val replyComment = Comment(
        id = "2", user = mockUser2, content = "确实，前奏一响就沦陷了。",
        createTime = System.currentTimeMillis(), likeCount = 88, isLiked = false, parentId = "1", rootId = "1",
        replyCount = 0
    )

    val previewUiState = CommentUiState(
        comments = listOf(
            CommentThread(
                rootComment = rootComment,
                replies = listOf(replyComment),
                totalReplyCount = 5,
                hasMoreReplies = true
            )
        )
    )

    MaterialTheme {
        CommentScreen(
            uiState = previewUiState,
            onBackClick = {},
            onLikeClick = { _, _ -> },
            onAddComment = { _, _ -> },
            onLoadMore = {},
            onViewMoreRepliesClick = {}
        )
    }
}
