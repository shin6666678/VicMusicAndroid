package com.shin.vicmusic.feature.likedSongs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.feature.song.ItemSong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedSongsRoute(
    onBackClick: () -> Unit,
    viewModel: LikedSongsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我喜欢的音乐") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is LikedSongsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is LikedSongsUiState.Error -> {
                    Text(
                        text = "错误: ${state.message}",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is LikedSongsUiState.Success -> {
                    LazyColumn {
                        items(state.songs) { song ->
                            // 使用现有的 ItemSong 组件
                            ItemSong(
                                data = song,
                                onAddToQueueClick = { /*TODO: 添加到播放队列逻辑*/ }
                            )
                        }
                    }
                }
            }
        }
    }
}