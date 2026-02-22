package com.shin.vicmusic.feature.song

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Feed
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.manager.PlayerState
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONG
import com.shin.vicmusic.feature.comment.CommentRoute
import com.shin.vicmusic.feature.common.bar.BarActionItem
import com.shin.vicmusic.feature.common.bar.CommonTopBarSelect
import com.shin.vicmusic.feature.feed.publish.navigateToPublishFeed
import com.shin.vicmusic.feature.song.component.LyricView
import com.shin.vicmusic.feature.song.component.PlaybackControlBar
import com.shin.vicmusic.feature.song.component.PlayerControls
import com.shin.vicmusic.feature.song.component.RecordPlayerView
import com.shin.vicmusic.feature.song.component.SongActionButtons
import com.shin.vicmusic.feature.song.component.SongInfoSection
import com.shin.vicmusic.util.QRCodeUtils
import com.shin.vicmusic.util.ResourceUtil
import com.shin.vicmusic.util.ShareUtils
import com.shin.vicmusic.util.captureComposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Preview
@Composable
fun SongDetailPreview() {
    SongDetailScreen(
        song = SONG,
        playerState = PlayerState(),
    )
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailRoute(
    songId: String? = null,
    onDismiss: () -> Unit,
    viewModel: SongDetailViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val playerManager = LocalPlayerManager.current
    val songUiState by viewModel.songUiState.collectAsState()
    val currentPlayingSong by playerManager.currentPlayingSong.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    var showVipDialog by remember { mutableStateOf(false) }
    var showShareBottomSheet by remember { mutableStateOf(false) }

    var showComments by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = showComments) {
        showComments = false
    }

    val context = LocalContext.current

    // 如果是通过参数传入的 songId，通知 ViewModel 加载
    LaunchedEffect(songId) {
        if (songId != null) {
            viewModel.setSongId(songId)
        }
    }

    DisposableEffect(Unit) {
        playerManager.isSongDetailVisible = true
        onDispose {
            playerManager.isSongDetailVisible = false
        }
    }

    LaunchedEffect(playerManager.uiEvent) {
        playerManager.uiEvent.collect { event ->
            when (event) {
                "SHOW_VIP_DIALOG" -> showVipDialog = true
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

    val parentComposition = rememberCompositionContext()

    val shareToOtherApps: () -> Unit = {
        coroutineScope.launch {
            val displaySong = (songUiState as? SongUiState.Success)?.song ?: return@launch
            val tag = "ShareProcess"
            try {
                // 1. 加载封面图 (IO 线程)
                val albumArtBitmap = withContext(Dispatchers.IO) {
                    val loader = context.imageLoader
                    val request = ImageRequest.Builder(context)
                        .data(ResourceUtil.r2(displaySong.icon))
                        .allowHardware(false)
                        .build()
                    (loader.execute(request) as? SuccessResult)?.let {
                        (it.drawable as android.graphics.drawable.BitmapDrawable).bitmap
                    }
                }

                // 2. 生成二维码 (H5 落地页 URL)
                val shareLandingUrl = "http://115.190.155.131:9001/share.html?id=${displaySong.id}"
                val qrBitmap = withContext(Dispatchers.Default) {
                    QRCodeUtils.createQRCode(shareLandingUrl, 200)
                }

                // 3. 渲染并截图 (Main 线程)
                val shareCardBitmap = withContext(Dispatchers.Main) {
                    captureComposable(context, parentComposition) {
                        SongShareCard(
                            song = displaySong,
                            albumArtBitmap = albumArtBitmap,
                            qrCodeBitmap = qrBitmap
                        )
                    }
                }

                // 4. 调用系统分享
                ShareUtils.shareSong(context, displaySong, shareCardBitmap)

            } catch (e: Exception) {
                Log.e(tag, "分享异常", e)
            }
        }
    }

    if (showShareBottomSheet) {
        ShareBottomSheet(
            onDismiss = { showShareBottomSheet = false },
            onShareToFeed = {
                val songIdToShare = (songUiState as? SongUiState.Success)?.song?.id
                if (songIdToShare != null) {
                    navController.navigateToPublishFeed(songIdToShare, "song")
                }
                showShareBottomSheet = false
            },
            onShareToOtherApps = { 
                shareToOtherApps()
                showShareBottomSheet = false 
            }
        )
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

            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1C1C1E))) {
                // 1. 背景层：高斯模糊封面
                val context = LocalContext.current
                var isLoading by remember { mutableStateOf(true) }

                androidx.compose.foundation.Image(
                    painter = coil.compose.rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(ResourceUtil.r2(displaySong.icon))
                            .crossfade(true)
                            .build(),
                        onLoading = { isLoading = true },
                        onSuccess = { isLoading = false }
                    ),
                    contentDescription = null,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(radius = 50.dp) // Android 12+ 生效
                )
                
                // 2. 遮罩层：半透明黑色，提升文字可读性
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)))

                // 3. 内容层：平移切换
                AnimatedContent(
                    targetState = showComments,
                    transitionSpec = {
                        if (targetState) {
                            (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                                    (slideOutHorizontally { width -> -width } + fadeOut())
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()) togetherWith
                                    (slideOutHorizontally { width -> width } + fadeOut())
                        }
                    },
                    label = "SongDetailContent",
                    modifier = Modifier.fillMaxSize()
                ) { isCommentVisible ->
                    if (isCommentVisible) {
                        // 评论页容器：必须透明
                        Box(modifier = Modifier.fillMaxSize()) {
                             CommentRoute(
                                 resourceId = displaySong.id,
                                 resourceType = "song",
                                 onBackClick = { showComments = false }
                             )
                        }
                    } else {
                        // 播放页容器：传入透明背景
                        SongDetailScreen(
                            song = displaySong,
                            playerState = playerState,
                            onTogglePlayPause = playerManager::togglePlayPause,
                            onSeek = playerManager::seekTo,
                            onBackClick = onDismiss,
                            onSkipNext = playerManager::skipToNext,
                            onSkipPrevious = playerManager::skipToPrevious,
                            onToggleLike = viewModel::toggleLike,
                            onCommentClick = {
                                 showComments = true
                            },
                            onShareClick = { showShareBottomSheet = true }
                        )
                    }
                }
            }
        }

        is SongUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "加载失败: ${uiState.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { viewModel.loadSongDetail(viewModel.songId ?: "") }) {
                        Text("重试")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    onDismiss: () -> Unit,
    onShareToFeed: () -> Unit,
    onShareToOtherApps: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalBottomSheetState,
    ) {
        Column {
            ListItem(
                headlineContent = { Text("分享到动态") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Feed,
                        contentDescription = "Share to Feed"
                    )
                },
                modifier = Modifier.clickable(onClick = onShareToFeed)
            )
            ListItem(
                headlineContent = { Text("分享到其他应用") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share to Other Apps"
                    )
                },
                modifier = Modifier.clickable(onClick = onShareToOtherApps)
            )
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
    onCommentClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
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
                        onClick = onShareClick
                    ),
                    BarActionItem(
                        icon = Icons.Default.List,
                        contentDescription = "其他功能",
                        onClick = {
                            onCommentClick()
                        }
                    ),
                ),
                contentColor = MaterialTheme.colorScheme.surface
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(Color.Transparent)
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
