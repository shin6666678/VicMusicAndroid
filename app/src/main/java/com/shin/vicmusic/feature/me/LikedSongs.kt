package com.shin.vicmusic.feature.me

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.feature.song.ItemSong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedSongsScreen(
    onBack: () -> Unit, // 返回回调
    viewModel: LikedSongsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 拦截系统返回键
    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我喜欢的音乐") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is LikedSongsUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is LikedSongsUiState.Error -> Text("错误: ${state.message}", modifier = Modifier.align(Alignment.Center))
                is LikedSongsUiState.Success -> {
                    LazyColumn {
                        items(state.songs) { song ->
                            ItemSong(data = song)
                        }
                    }
                }
            }
        }
    }
}