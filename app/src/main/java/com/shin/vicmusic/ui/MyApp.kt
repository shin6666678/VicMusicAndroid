package com.shin.vicmusic.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.shin.vicmusic.core.design.component.MyNavigationBar
import com.shin.vicmusic.core.design.component.SongBar
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.feature.auth.loginScreen
import com.shin.vicmusic.feature.auth.navigateToLogin
import com.shin.vicmusic.feature.auth.registerScreen
import com.shin.vicmusic.feature.discovery.artistListScreen
import com.shin.vicmusic.feature.main.MAIN_ROUTE
import com.shin.vicmusic.feature.main.TopLevelDestination
import com.shin.vicmusic.feature.main.mainScreen
import com.shin.vicmusic.feature.main.navigateToMain
import com.shin.vicmusic.feature.search.searchScreen
import com.shin.vicmusic.feature.song.navigateToSongDetail
import com.shin.vicmusic.feature.song.songDetailScreen
import com.shin.vicmusic.feature.splash.SPLASH_ROUTE
import com.shin.vicmusic.feature.splash.splashScreen
import androidx.compose.foundation.layout.offset // 确保导入
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.zIndex
import com.shin.vicmusic.core.design.theme.SpaceExtraMedium
import com.shin.vicmusic.core.design.theme.SpaceOuter
import com.shin.vicmusic.feature.song.SONG_DETAIL_ROUTE

@Composable
fun MyApp(navController: NavHostController) {
    val playerManager = LocalPlayerManager.current
    val currentSong by playerManager.currentPlayingSong.collectAsState()
    val playerState by playerManager.playerState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val mainTabState = rememberSaveable { mutableIntStateOf(0) }

    // 1. 定义状态
    val isMainScreen = currentRoute == MAIN_ROUTE
    val isSplashScreen = currentRoute == SPLASH_ROUTE
    val isSongDetail = currentRoute?.contains("songDetail") == true
    val showBottomContainer = !isSplashScreen&&!isSongDetail

    // 2. 定义位移量
    // 如果是主页 -> 位移 0dp
    // 如果不是主页 -> 位移 80dp
    val navBarTranslationY by animateDpAsState(
        targetValue = if (isMainScreen) 0.dp else 70.dp,
        animationSpec = tween(2200),
        label = "navBarTranslation"
    )
    val songBarHalfHeight = 27.dp // 决定背景覆盖多少，通常是 SongBar 高度的一半左右

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = SPLASH_ROUTE,
            modifier = Modifier.fillMaxSize()
        ) {
            splashScreen(toMain = navController::navigateToMain, toLogin = navController::navigateToLogin)
            mainScreen(finishPage = navController::popBackStack, navController = navController, mainTabState = mainTabState)
            loginScreen(navController)
            songDetailScreen(navController = navController)
            registerScreen(navController = navController)
            searchScreen(navController = navController)
            artistListScreen(navController = navController)
        }

        // 底部整体容器 (SongBar + 导航栏)
        AnimatedVisibility(
            visible = showBottomContainer,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = navBarTranslationY)
            ) {
                // 层级 1：背景层 + 导航栏
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(top = songBarHalfHeight)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    // 一个"隐形垫片"，高度等于 Padding 的高度。
                    Spacer(modifier = Modifier.height(songBarHalfHeight))
                    //空白部分
                    Spacer(modifier = Modifier.height(SpaceExtraMedium))
                    MyNavigationBar(
                        destinations = TopLevelDestination.entries,
                        currentDestination = TopLevelDestination.entries[mainTabState.intValue].route,
                        onNavigateToDestination = { index -> mainTabState.intValue = index },
                        modifier = Modifier.fillMaxWidth().height(70.dp)
                    )
                }

                // 层级2: SongBar
                if (currentSong != null) {
                    SongBar(
                        song = currentSong!!,
                        playerState = playerState,
                        onTogglePlayPause = playerManager::togglePlayPause,
                        onLikeClick = { /* Like */ },
                        onPlaylistClick = { /* Playlist */ },
                        onBarClick = { navController.navigateToSongDetail(currentSong!!.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter) // 对齐顶部
                            .zIndex(1f) // 确保浮在背景层上面
                            .padding(horizontal = 18.dp)
                    )
                }
            }
        }
    }
}