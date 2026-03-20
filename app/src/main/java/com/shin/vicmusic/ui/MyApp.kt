package com.shin.vicmusic.ui

import android.Manifest
import android.R.attr.label
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import com.shin.vicmusic.core.design.theme.LocalAppColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.shin.vicmusic.core.design.LocalMainViewModel
import com.shin.vicmusic.core.design.composition.LocalAuthManager
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.design.theme.AppBackground
import com.shin.vicmusic.core.design.theme.VicMusicBackgroundGateway
import com.shin.vicmusic.core.manager.PlayerUiEvent
import com.shin.vicmusic.feature.album.albumDetail.albumDetailScreen
import com.shin.vicmusic.feature.album.albumList.albumListScreen
import com.shin.vicmusic.feature.artist.artistDetail.artistDetailScreen
import com.shin.vicmusic.feature.artist.artistList.artistListScreen
import com.shin.vicmusic.feature.auth.LOGIN_ROUTE
import com.shin.vicmusic.feature.auth.REGISTER_ROUTE
import com.shin.vicmusic.feature.auth.loginScreen
import com.shin.vicmusic.feature.auth.navigateToLogin
import com.shin.vicmusic.feature.auth.registerScreen
import com.shin.vicmusic.feature.chat.chatScreen
import com.shin.vicmusic.feature.checkIn.CHECK_IN_ROUTE
import com.shin.vicmusic.feature.checkIn.checkInScreen
import com.shin.vicmusic.feature.comment.CommentRoute
import com.shin.vicmusic.feature.common.MyNavigationBar
import com.shin.vicmusic.feature.common.bar.SongBar
import com.shin.vicmusic.feature.feed.feedScreen
import com.shin.vicmusic.feature.feed.publish.publishFeedScreen
import com.shin.vicmusic.feature.me.dressup.dressUpScreen
import com.shin.vicmusic.feature.me.audio.audioScreen
import com.shin.vicmusic.feature.me.purchased.purchasedScreen
import com.shin.vicmusic.feature.liked.likedListScreen
import com.shin.vicmusic.feature.localMusic.localMusicScreen
import com.shin.vicmusic.feature.main.MAIN_ROUTE
import com.shin.vicmusic.feature.main.MainViewModel
import com.shin.vicmusic.feature.main.TopLevelDestination
import com.shin.vicmusic.feature.main.mainScreen
import com.shin.vicmusic.feature.me.recentPlay.recentPlayScreen
import com.shin.vicmusic.feature.me.setting.settingScreen
import com.shin.vicmusic.feature.message.messageListScreen
import com.shin.vicmusic.feature.myInfo.edit.myInfoEditScreen
import com.shin.vicmusic.feature.myInfo.myInfoScreen
import com.shin.vicmusic.feature.playBackQueue.PlaybackQueueSheet
import com.shin.vicmusic.feature.common.dialog.CopyrightDialog
import com.shin.vicmusic.feature.playlist.detail.playlistDetailScreen
import com.shin.vicmusic.feature.playlist.daily.dailyPlaylistScreen
import com.shin.vicmusic.feature.playlist.meList.myPlaylistScreen
import com.shin.vicmusic.feature.playlist.publicList.publicPlaylistScreen
import com.shin.vicmusic.feature.rankList.rankList.rankListScreen
import com.shin.vicmusic.feature.rankList.rankListDetail.rankListDetailScreen
import com.shin.vicmusic.feature.relationship.relationshipScreen
import com.shin.vicmusic.feature.search.searchScreen
import com.shin.vicmusic.feature.song.SongDetailRoute
import com.shin.vicmusic.feature.splash.SPLASH_ROUTE
import com.shin.vicmusic.feature.splash.splashScreen
import com.shin.vicmusic.feature.vip.VIP_ROUTE
import com.shin.vicmusic.feature.vip.vipScreen

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(
) {

    val mainViewModel = LocalMainViewModel.current
    val navController = LocalNavController.current
    val playerManager = LocalPlayerManager.current
    val authManager = LocalAuthManager.current
    val playerUiState by playerManager.uiState.collectAsState()

    val currentSong = playerUiState.song
    val playerState = playerUiState.isPlaying
    val isLoggedIn by authManager.isLoggedIn.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val mainTabState = rememberSaveable { mutableIntStateOf(0) }
    val unreadCount by mainViewModel.unreadCount.collectAsState()

    var showCopyrightDialog by rememberSaveable { mutableStateOf(false) }

    // 全局未登录拦截：当确认未登录时，强制跳转登录页并清空回退栈
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == false) {
            val currentDest = navController.currentBackStackEntry?.destination?.route
            if (currentDest != LOGIN_ROUTE && currentDest != REGISTER_ROUTE) {
                navController.navigateToLogin()
            }
        }
    }

    // 监听全局 UI 事件 (如版权弹窗)
    LaunchedEffect(playerManager.uiEvent) {
        playerManager.uiEvent.collect { event ->
            when (event) {
                PlayerUiEvent.ShowCopyrightDialog -> showCopyrightDialog = true
                else -> {}
            }
        }
    }

    // Android 13+ 请求通知权限
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                Log.d("MyApp", "Notification permission granted: $isGranted")
            }
        )
        // 应用启动时检查并请求权限
        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val badgeCounts = mapOf(
        TopLevelDestination.ME to unreadCount
        // TopLevelDestination.FEED to feedUnreadCount // 未来扩展
    )

    // 1. 定义状态
    val isMainScreen = currentRoute == MAIN_ROUTE
    val isSplashScreen = currentRoute == SPLASH_ROUTE
    val isSongDetail = currentRoute?.contains("songDetail") == true
    val isChatScreen = currentRoute?.contains("chat") == true
    val isVipScreen = currentRoute == VIP_ROUTE
    val isCommentScreen = currentRoute?.contains("comment") == true
    val showBottomContainer = currentRoute != null
            && !isSplashScreen && !isSongDetail
            && !isVipScreen
            && !isChatScreen
            && !isCommentScreen

    val playQueue by playerManager.playbackQueue.collectAsState()
    val currentQueueIndex by playerManager.currentQueueIndex.collectAsState()

    // 播放列表弹窗显示状态
    var showPlaylistSheet by rememberSaveable { mutableStateOf(false) }
    // 歌曲详情弹窗显示状态
    var showSongDetailSheet by rememberSaveable { mutableStateOf(false) }

    // BottomSheet 状态
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 2. 定义位移量
    // 如果是主页 -> 位移 0dp
    // 如果不是主页 -> 位移 80dp
    val navBarTranslationY by animateDpAsState(
        targetValue = if (isMainScreen) 0.dp else 75.dp,
        animationSpec = tween(2000),
        label = "navBarTranslation"
    )


    val appColors = LocalAppColors.current

    val bottomContainerColor by animateColorAsState(
        targetValue = appColors.bottomBarBackground,
        label = "bottomContainerColor"
    )

    val bottomContentColor by animateColorAsState(
        targetValue = appColors.bottomBarContent,
        label = "bottomContentColor"
    )

    VicMusicBackgroundGateway {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = SPLASH_ROUTE,
                modifier = Modifier.fillMaxSize()
            ) {
                splashScreen()
                mainScreen(mainTabState = mainTabState)
                loginScreen()
                registerScreen()
                searchScreen()
                artistListScreen()
                rankListScreen()
                vipScreen()
                artistDetailScreen()
                rankListDetailScreen()
                albumListScreen()
                albumDetailScreen()
                playlistDetailScreen()
                dailyPlaylistScreen()
                myPlaylistScreen()
                likedListScreen()
                recentPlayScreen()
                myInfoScreen()
                publicPlaylistScreen()
                checkInScreen()
                settingScreen()
                relationshipScreen()
                chatScreen()
                messageListScreen()
                localMusicScreen()
                feedScreen()
                myInfoEditScreen()
                publishFeedScreen()
                dressUpScreen(popBackStack = { navController.popBackStack() })
                audioScreen(onBackClick = { navController.popBackStack() })
                purchasedScreen(onBackClick = { navController.popBackStack() })
            }

            // 底部整体容器 (SongBar + 导航栏)
            AnimatedVisibility(
                visible = showBottomContainer,
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = navBarTranslationY)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    bottomContainerColor.copy(alpha = 0.7f),
                                    bottomContainerColor.copy(alpha = 1f)
                                )
                            )
                        )
                ) {
                    // 如果当前有歌曲，就显示 SongBar
                    if (currentSong != null) {
                        SongBar(
                            song = currentSong,
                            playerUiState = playerUiState,
                            onTogglePlayPause = playerManager::togglePlayPause,
                            onPlaylistClick = { showPlaylistSheet = true },
                            onBarClick = { showSongDetailSheet = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }

                    MyNavigationBar(
                        destinations = TopLevelDestination.entries,
                        currentDestination = TopLevelDestination.entries[mainTabState.intValue].route,
                        onNavigateToDestination = { index -> mainTabState.intValue = index },
                        badgeCounts = badgeCounts,
                        containerColor = Color.Transparent,
                        contentColor = bottomContentColor,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // 播放列表弹窗 (Bottom Sheet)
            if (showPlaylistSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showPlaylistSheet = false },
                    sheetState = sheetState,
                    containerColor = LocalAppColors.current.textColor,
                    dragHandle = null
                ) {
                    PlaybackQueueSheet(
                        isPlayingQueue = playQueue,
                        currentIndex = currentQueueIndex,
                        onSongClick = playerManager::playAtIndex,
                        onRemoveSong = playerManager::removeSong,
                    )
                }
            }

            // 歌曲详情页 Edge-to-Edge 覆盖层
            AnimatedVisibility(
                visible = showSongDetailSheet,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.fillMaxSize()
            ) {
                androidx.activity.compose.BackHandler {
                    showSongDetailSheet = false
                }
                SongDetailRoute(
                    songId = currentSong?.id,
                    onDismiss = {
                        showSongDetailSheet = false
                    }
                )
            }

            // Global Copyright Dialog
            if (showCopyrightDialog) {
                CopyrightDialog(
                    song = currentSong,
                    onDismissRequest = { showCopyrightDialog = false }
                )
            }
        }
    }

}