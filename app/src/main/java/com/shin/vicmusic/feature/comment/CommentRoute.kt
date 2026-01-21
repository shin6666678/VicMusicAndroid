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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.feature.common.item.ItemComment
import com.shin.vicmusic.core.domain.Comment
@Composable
fun CommentRoute(
    resourceId: String,
    resourceType: String,
    viewModel: CommentViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()

    // 当页面首次加载时，根据传入的资源ID和类型加载评论
    LaunchedEffect(resourceId, resourceType) {
        viewModel.loadComments(
            resourceType = resourceType,
            resourceId = resourceId,
            isRefresh = true
        )
    }

    CommentScreen(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onLikeClick = viewModel::toggleLike,
        onAddComment = { content ->
            viewModel.addComment(resourceType, resourceId, content)
        },
        onLoadMore = {
            viewModel.loadComments(resourceType, resourceId)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    uiState: CommentUiState,
    onBackClick: () -> Unit,
    onLikeClick: (String) -> Unit,
    onAddComment: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }

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
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            // 底部评论输入框
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                placeholder = { Text("留下你的精彩评论吧") },
                trailingIcon = {
                    TextButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                onAddComment(commentText)
                                commentText = "" // 发送后清空
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
                    items(uiState.comments) { comment ->
                        // 使用您移动后的 CommentItem
                        ItemComment(comment = comment, onLikeClick = onLikeClick)
                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    }

                    // 加载更多逻辑
                    if (uiState.hasMore) {
                        item {
                            // 当这个item对用户可见时，触发加载更多
                            LaunchedEffect(uiState.comments.size) {
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
}

// 预览函数
@Preview(showBackground = true, backgroundColor = 0xFF1C1C1E)
@Composable
fun CommentScreenPreview() {
    // 1. 创建一个用于预览的模拟 UI 状态
    val previewUiState = CommentUiState(
        comments = listOf(
            Comment(
                id = "1",
                userId = "user1",
                userName = "Shin",
                userHeadImg = "",
                resourceType = "song",
                resourceId = "song1",
                content = "这首歌太好听了，单曲循环一整天！",
                likeCount = 102,
                liked = true,
                createTime = System.currentTimeMillis()
            ),
            Comment(
                id = "2",
                userId = "user2",
                userName = "音乐爱好者",
                userHeadImg = "",
                resourceType = "song",
                resourceId = "song1",
                content = "前奏一响，青春就回来了。评论区的朋友们，你们好吗？",
                likeCount = 88,
                liked = false,
                createTime = System.currentTimeMillis() - 1000 * 60 * 5
            ),
            Comment(
                id = "3",
                userId = "user3",
                userName = "Vic",
                userHeadImg = "",
                resourceType = "song",
                resourceId = "song1",
                content = "有人知道这是哪部电影的插曲吗？感觉很熟悉。",
                likeCount = 15,
                liked = false,
                createTime = System.currentTimeMillis() - 1000 * 60 * 30
            )
        ),
        isLoading = false,
        error = null,
        page = 1,
        hasMore = true
    )

    // 2. 使用模拟状态来渲染 CommentScreen
    MaterialTheme {
        CommentScreen(
            uiState = previewUiState,
            onBackClick = {},
            onLikeClick = {},
            onAddComment = {},
            onLoadMore = {}
        )
    }
}

