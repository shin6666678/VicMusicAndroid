package com.shin.vicmusic.feature.feed.publish

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.feature.common.bar.SongBar
import com.shin.vicmusic.feature.common.item.ItemSong
import com.shin.vicmusic.feature.song.component.SongInfoSection

@Composable
fun PublishFeedRoute(
    viewModel: PublishFeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val navController= LocalNavController.current
    // 处理副作用
    LaunchedEffect(uiState.isPublished) {
        if (uiState.isPublished) {
            Toast.makeText(context, "发布成功", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    // 路由层只负责把 State 和 Event 传给 Screen
    PublishFeedScreen(
        uiState = uiState,
        onBack = navController::popBackStack,
        onPublishClick = { comment -> viewModel.publishFeed(comment) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishFeedScreen(
    uiState: PublishFeedUiState, // 假设你的 UI State 类名
    onBack: () -> Unit,
    onPublishClick: (String) -> Unit
) {
    var comment by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("发布动态") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = { onPublishClick(comment) },
                        enabled = !uiState.isLoading && comment.isNotBlank()
                    ) {
                        Text("发布")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("今天想说点什么...") },
                maxLines = 10
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 预览分享的内容
            uiState.sharedContent?.let { content ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    when (content) {
                        is Song -> ItemSong(song = content)
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PublishFeedScreenPreview() {
    // 构造模拟数据
    val mockState = PublishFeedUiState(
        isLoading = false,
        isPublished = false,
        sharedContent = Song(
            id = "1",
            title = "Mock Song Title",
        )
    )

    MaterialTheme {
        PublishFeedScreen(
            uiState = mockState,
            onBack = {},
            onPublishClick = {}
        )
    }
}
