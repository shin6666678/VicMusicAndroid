package com.shin.vicmusic.feature.liked

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.feature.album.albumDetail.navigateToAlbumDetail
import com.shin.vicmusic.feature.common.bar.CommonTopBar
import com.shin.vicmusic.feature.liked.component.LikedAlbum
import com.shin.vicmusic.feature.liked.component.LikedPlayList
import com.shin.vicmusic.feature.liked.component.LikedSong
import com.shin.vicmusic.feature.playlist.detail.navigateToPlaylistDetail

@Composable
fun LikedRoute(
    viewModel: LikedSongsViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()
    val currentTabIndex by viewModel.tabIndex.collectAsState()

    val context = LocalContext.current
    
    // 使用 LaunchedEffect(Unit) 确保只订阅一次
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LikedScreen(
        onBack = navController::popBackStack,
        uiState = uiState,
        currentTabIndex = currentTabIndex,
        onTabChange = viewModel::switchTab,
        onAlbumClick = navController::navigateToAlbumDetail,
        onPlayListClick = navController::navigateToPlaylistDetail,
        onToggleLike = viewModel::toggleLike
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedScreen(
    onBack: () -> Unit = {},
    uiState: LikedSongsUiState,
    currentTabIndex: Int,
    onTabChange: (Int) -> Unit = {},
    onAlbumClick: (String) -> Unit = {},
    onPlayListClick: (String) -> Unit = {},
    onToggleLike: (Song) -> Unit = {}
) {
    Scaffold(
        topBar = {
            CommonTopBar(
                midText = "我的收藏",
                showSearch = false,
                popBackStack = onBack,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = currentTabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.background
            ) {
                val tabs = listOf("歌曲", "专辑", "歌单")
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = currentTabIndex == index,
                        onClick = { onTabChange(index) }, // 点击时，通知上层修改
                        text = { Text(title) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (val state = uiState) {
                    is LikedSongsUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is LikedSongsUiState.Error -> {
                        Text(
                            text = "错误: ${state.message}",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is LikedSongsUiState.Success -> {
                        // 根据传入的 currentTabIndex 渲染对应的内容
                        when (currentTabIndex) {
                            0 -> LikedSong(
                                songs = state.songs
                            )
                            1 -> LikedAlbum(
                                albums = state.albums,
                                onClick = onAlbumClick
                            )
                            2 -> LikedPlayList(
                                playLists = state.playlists,
                                onClick = onPlayListClick
                            )
                        }
                    }
                }
            }
        }
    }
}