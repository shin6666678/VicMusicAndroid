package com.shin.vicmusic.feature.song

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.manager.PlayerState
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONG
import com.shin.vicmusic.feature.song.component.LyricView
import com.shin.vicmusic.feature.song.component.PlaybackControlBar
import com.shin.vicmusic.feature.song.component.PlayerControls
import com.shin.vicmusic.feature.song.component.RecordPlayerView
import com.shin.vicmusic.feature.song.component.SongActionButtons
import com.shin.vicmusic.feature.song.component.SongDetailTopBar
import com.shin.vicmusic.feature.song.component.SongInfoSection

@Preview
@Composable
fun SongDetailPreview() {
    SongDetailScreen(
        song = SONG,
        playerState = PlayerState(),
    )
}

@Composable
fun SongDetailRoute(
    viewModel: SongDetailViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val playerManager = LocalPlayerManager.current
    val songUiState by viewModel.songUiState.collectAsState()
    val currentPlayingSong by playerManager.currentPlayingSong.collectAsState()

    // 状态：是否显示VIP弹窗
    var showVipDialog by remember { mutableStateOf(false) }

    // 核心逻辑：监听进入和离开 SongDetail 界面，更新 PlayerManager 中的标志位
    DisposableEffect(Unit) {
        playerManager.isSongDetailVisible = true
        onDispose {
            playerManager.isSongDetailVisible = false
        }
    }

    // 核心逻辑：监听 PlayerManager 发出的事件
    LaunchedEffect(playerManager.uiEvent) {
        playerManager.uiEvent.collect { event ->
            if (event == "SHOW_VIP_DIALOG") {
                showVipDialog = true
            }
        }
    }

    // 弹窗UI
    if (showVipDialog) {
        AlertDialog(
            onDismissRequest = { showVipDialog = false },
            title = { Text("VIP 试听结束") },
            text = { Text("本歌曲为 VIP 专享，试听已结束。请开通 VIP 继续收听。") },
            confirmButton = {
                TextButton(onClick = {
                    showVipDialog = false
                    // 这里可以添加跳转到VIP购买页面的逻辑
                }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showVipDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    LaunchedEffect(songUiState) {
        if (songUiState is SongUiState.Success) {
            val song = (songUiState as SongUiState.Success).song
            if (currentPlayingSong?.id != song.id) {
                playerManager.playSong(song)
            }
        }
    }

    when (val uiState = songUiState) {
        is SongUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is SongUiState.Success -> {
            val playerState by playerManager.playerState.collectAsState()
            val displaySong = if (currentPlayingSong?.id == uiState.song.id) {
                currentPlayingSong!!
            } else {
                uiState.song
            }
            SongDetailScreen(
                song = displaySong,
                playerState = playerState,
                onTogglePlayPause = playerManager::togglePlayPause,
                onSeek = playerManager::seekTo,
                onBackClick = { navController.popBackStack() },
                onSkipNext = playerManager::skipToNext,
                onSkipPrevious = playerManager::skipToPrevious,
                onToggleLike = viewModel::toggleLike
            )
        }
        is SongUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "加载失败: ${uiState.message}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadSongDetail(viewModel.songId ?: "") }) {
                        Text("重试")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongDetailScreen(
    song: Song,
    playerState: PlayerState,
    onTogglePlayPause: () -> Unit={},
    onSeek: (Long) -> Unit={},
    onBackClick: () -> Unit={},
    onSkipNext: () -> Unit={},
    onSkipPrevious: () -> Unit={},
    onToggleLike: () -> Unit={}
) {
    // 0: 唱片机页面, 1: 歌词页面
    val pagerState = rememberPagerState(pageCount = { 2 })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1E))
    ) {
        SongDetailTopBar(onBackClick = onBackClick)

        // 使用 Pager 实现滑动切换
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            if (page == 0) {
                // 页面 0: 唱片机 + 信息
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    RecordPlayerView(albumArtUrl = song.icon, isPlaying = playerState.isPlaying)
                    Spacer(modifier = Modifier.height(32.dp))
                    SongInfoSection(song = song)
                    Spacer(modifier = Modifier.weight(1f))
                    // 底部控制区域 (始终显示)
                    Column {
                        SongActionButtons(isLiked = song.isLiked, onLikeClick = onToggleLike)
                        Spacer(modifier = Modifier.height(16.dp))
                        PlaybackControlBar(playerState = playerState, onSeek = onSeek)
                        PlayerControls(
                            playerState = playerState,
                            onTogglePlayPause = onTogglePlayPause,
                            onNextClick = onSkipNext,
                            onPreviousClick = onSkipPrevious
                        )
                    }
                }
            } else {
                // 页面 1: 歌词
                Log.d("1111111111111111",song.toString())
                LyricView(
                    lyricList = song.lyricList,
                    currentIndex = playerState.currentLyricLineIndex,
                    onLineClick = onSeek
                )
            }
        }
    }
}