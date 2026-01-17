package com.shin.vicmusic.feature.song

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.shin.vicmusic.feature.comment.navigateToComment
import com.shin.vicmusic.feature.common.bar.BarActionItem
import com.shin.vicmusic.feature.common.bar.CommonTopBar
import com.shin.vicmusic.feature.common.bar.CommonTopBarSelect
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

    var showVipDialog by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        playerManager.isSongDetailVisible = true
        onDispose {
            playerManager.isSongDetailVisible = false
        }
    }

    LaunchedEffect(playerManager.uiEvent) {
        playerManager.uiEvent.collect { event ->
            if (event == "SHOW_VIP_DIALOG") {
                showVipDialog = true
            }
        }
    }

    if (showVipDialog) {
        AlertDialog(
            onDismissRequest = { showVipDialog = false },
            title = { Text("VIP 试听结束") },
            text = { Text("本歌曲为 VIP 专享，试听已结束。请开通 VIP 继续收听。") },
            confirmButton = {
                TextButton(onClick = { showVipDialog = false }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showVipDialog = false }) { Text("取消") }
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
                onToggleLike = viewModel::toggleLike,
                onCommentClick = {
                    navController.navigateToComment(
                        resourceId = displaySong.id,
                        resourceType = "song"
                    )
                }
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
    onTogglePlayPause: () -> Unit = {},
    onSeek: (Long) -> Unit = {},
    onBackClick: () -> Unit = {},
    onSkipNext: () -> Unit = {},
    onSkipPrevious: () -> Unit = {},
    onToggleLike: () -> Unit = {},
    onCommentClick: () -> Unit = {} // 新增评论点击事件
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    Scaffold(
        topBar = {
            CommonTopBarSelect(
                backImageVictor = Icons.Default.KeyboardArrowDown,
                onBackClick = onBackClick,
                actions = listOf(
                    BarActionItem(
                        icon = Icons.Default.Share,
                        contentDescription = "分享",
                        onClick = {}
                    ),
                    BarActionItem(
                        icon = Icons.Default.List,
                        contentDescription = "其他功能",
                        onClick = {}
                    ),
                )
            )
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .background(Color(0xFF1C1C1E))
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                if (page == 0) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(35.dp))
                        RecordPlayerView(albumArtUrl = song.icon, isPlaying = playerState.isPlaying)
                        Spacer(modifier = Modifier.height(28.dp))
                        SongInfoSection(song = song)
                        Spacer(modifier = Modifier.height(28.dp))
                        Column {
                            // 将 onCommentClick 传递给 SongActionButtons
                            SongActionButtons(
                                isLiked = song.isLiked,
                                onLikeClick = onToggleLike,
                                onCommentClick = onCommentClick // 传递事件
                            )
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
                    LyricView(
                        lyricList = song.lyricList,
                        currentIndex = playerState.currentLyricLineIndex,
                        onLineClick = onSeek
                    )
                }
            }
        }
    }
}
