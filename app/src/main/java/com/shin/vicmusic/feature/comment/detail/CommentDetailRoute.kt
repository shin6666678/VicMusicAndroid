package com.shin.vicmusic.feature.comment.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Comment
import com.shin.vicmusic.feature.common.item.ItemCommentThread

@Composable
fun CommentDetailRoute(
    viewModel: CommentDetailViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()

    CommentDetailScreen(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onLikeClick = { _, _ -> /* TODO */ },
        onAddComment = viewModel::addComment
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentDetailScreen(
    uiState: CommentDetailUiState,
    onBackClick: () -> Unit,
    onLikeClick: (commentId: String, rootId: String?) -> Unit,
    onAddComment: (content: String, parentId: String?) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var replyToComment by remember { mutableStateOf<Comment?>(null) }

    val placeholderText = if (replyToComment != null) {
        "回复 @${replyToComment?.user?.name}:"
    } else {
        "回复 @${uiState.commentDetail?.rootComment?.user?.name}:"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("评论详情") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        bottomBar = {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).navigationBarsPadding(),
                placeholder = { Text(placeholderText) },
                trailingIcon = {
                    TextButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                onAddComment(commentText, replyToComment?.id)
                                commentText = ""
                                replyToComment = null // Reset reply state
                            }
                        }
                    ) {
                        Text("发送")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(text = "Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center))
                }
                uiState.commentDetail != null -> {
                    val detail = uiState.commentDetail
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            val rootThread = com.shin.vicmusic.core.domain.CommentThread(
                                rootComment = detail.rootComment,
                                replies = emptyList(),
                                totalReplyCount = detail.allReplies.size,
                                hasMoreReplies = false
                            )
                            ItemCommentThread(
                                thread = rootThread,
                                onLikeClick = onLikeClick,
                                onReplyClick = { _, reply -> replyToComment = reply ?: detail.rootComment },
                                onViewMoreRepliesClick = {}
                            )
                            Divider()
                        }

                        items(detail.allReplies, key = { it.id }) {
                            val replyThread = com.shin.vicmusic.core.domain.CommentThread(
                                rootComment = it,
                                replies = emptyList(),
                                totalReplyCount = 0,
                                hasMoreReplies = false
                            )
                            ItemCommentThread(
                                thread = replyThread,
                                onLikeClick = { commentId, _ -> onLikeClick(commentId, detail.rootComment.id) },
                                onReplyClick = { _, _ -> replyToComment = it },
                                onViewMoreRepliesClick = {}
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}
