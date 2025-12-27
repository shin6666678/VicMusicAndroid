package com.shin.vicmusic.feature.liked

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.feature.liked.component.LikedAlbum
import com.shin.vicmusic.feature.liked.component.LikedPlayList
import com.shin.vicmusic.feature.liked.component.LikedSong

@Preview
@Composable
fun LikedScreenPreview() {
    LikedScreen(onBack = {})
}
@Composable
fun LikedRoute(
    viewModel: LikedSongsViewModel = hiltViewModel()
) {
    val navController= LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()
    LikedScreen(
        onBack = navController::popBackStack,
        uiState = uiState
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedScreen(
    onBack: () -> Unit={},
    uiState: LikedSongsUiState = LikedSongsUiState.Loading,
) {

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0 for 歌曲, 1 for 专辑

    // 拦截系统返回键
    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            Column {
                // Custom Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                    Text(
                        text = "我的收藏",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Row {
                        IconButton(onClick = { /*TODO: Share action*/ }) {
                            Icon(Icons.Default.Share, contentDescription = "分享")
                        }
                        IconButton(onClick = { /*TODO: More options action*/ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "更多")
                        }
                    }
                }

                // Tabs: 歌曲 and 专辑
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.background // 使用背景色以保持一致
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("歌曲") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("专辑") }
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        text = { Text("歌单") }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            when (val state = uiState) {
                is LikedSongsUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is LikedSongsUiState.Error -> Text("错误: ${state.message}", modifier = Modifier.align(Alignment.Center))
                is LikedSongsUiState.Success -> {
                    if (selectedTabIndex == 0) { // 歌曲 Tab
                        LikedSong(state.songs)
                    } else if(selectedTabIndex == 1) { // 专辑 Tab
                        LikedAlbum()
                    } else { // 歌单 Tab
                        LikedPlayList()
                    }
                }
            }
        }
    }
}
