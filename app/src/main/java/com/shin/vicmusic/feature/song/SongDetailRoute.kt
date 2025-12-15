package com.shin.vicmusic.feature.song

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.feature.player.PlayerState
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONG
import com.shin.vicmusic.feature.song.component.PlaybackControlBar
import com.shin.vicmusic.feature.song.component.PlayerControls
import com.shin.vicmusic.feature.song.component.RecordPlayerView
import com.shin.vicmusic.feature.song.component.SongActionButtons
import com.shin.vicmusic.feature.song.component.SongDetailTopBar
import com.shin.vicmusic.feature.song.component.SongInfoSection


@Composable
fun SongDetailRoute(
    navController: NavController,                                  // 接收 NavController
    viewModel: SongDetailViewModel = hiltViewModel(),        // 获取歌曲详情的 ViewModel
) {
    val playerManager = LocalPlayerManager.current
    // 观察歌曲数据的加载状态
    val songUiState by viewModel.songUiState.collectAsState()
    val currentPlayingSong by playerManager.currentPlayingSong.collectAsState() // 监听当前播放歌曲

    LaunchedEffect(songUiState) {
        if (songUiState is SongUiState.Success) {
            val song = (songUiState as SongUiState.Success).song
            // 避免重复播放（当前播放的就是这首歌时，不重复调用）
            if (currentPlayingSong?.id != song.id) {
                playerManager.playSong(song)
            }
        }
    }

    // 渲染UI，根据不同的状态显示不同内容
    when (val uiState = songUiState) {
        is SongUiState.Loading -> {
            // 加载中状态，显示加载指示器
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is SongUiState.Success -> {
            // 数据加载成功，渲染歌曲详情屏幕
            // 从全局 PlayerViewModel 获取播放状态
            val playerState by playerManager.playerState.collectAsState()
            SongDetailScreen(
                song = uiState.song,
                playerState = playerState,
                onTogglePlayPause = playerManager::togglePlayPause, // 传递播放/暂停回调
                onSeek = playerManager::seekTo,                   // 传递跳转回调
                onBackClick = { navController.popBackStack() },
                onSkipNext = playerManager::skipToNext,           // 传递下一首回调
                onSkipPrevious = playerManager::skipToPrevious  ,    // 传递上一首回调
                // [新增] 传递 ViewModel 的 toggleLike 方法
                onToggleLike = viewModel::toggleLike
            )
        }
        is SongUiState.Error -> {
            // 加载失败状态，显示错误信息和重试按钮
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "加载失败: ${uiState.message}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadSongDetail(viewModel.songId ?: "") }) { // 重试按钮
                        Text("重试")
                    }
                }
            }
        }
    }
}

@Composable
fun SongDetailScreen(
    song: Song,
    playerState: PlayerState,
    onTogglePlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onBackClick: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleLike: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1E)) // 背景颜色，根据设计图硬编码一个深灰色
    ) {
        // 顶部导航栏
        SongDetailTopBar(onBackClick = onBackClick)

        // 唱片机视图
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
             RecordPlayerView(albumArtUrl = song.icon, isPlaying = playerState.isPlaying) // 传递 isPlaying 状态
        }
        Spacer(modifier = Modifier.height(32.dp))

        // 歌曲信息部分
        SongInfoSection(song = song)

        // 操作按钮行
        SongActionButtons(
            isLiked = song.isLiked,   // 从 Song 对象中获取状态
            onLikeClick = onToggleLike // 绑定回调
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 播放进度条
        PlaybackControlBar(playerState = playerState, onSeek = onSeek)

        // 播放控制按钮
        PlayerControls(
            playerState = playerState,
            onTogglePlayPause = onTogglePlayPause,
            onNextClick = onSkipNext,
            onPreviousClick = onSkipPrevious
        )
    }
}

@Preview
@Composable
fun SongDetailPreView() {
    VicMusicTheme {
        SongDetailScreen(
            song = SONG, // 使用 DiscoveryPreviewParameterData.SONG 进行预览
            playerState = PlayerState(
                isPlaying = false,
                duration = 100000,
                currentPosition = 50000
            ),
            onTogglePlayPause = { /* Do nothing for preview */ },
            onSeek = { /* Do nothing for preview */ },
            onBackClick = { /* Do nothing for preview */ },
            onSkipNext = { /* Do nothing for preview */ },
            onSkipPrevious = { /* Do nothing for preview */ },
            onToggleLike = { }
        )
    }
}
