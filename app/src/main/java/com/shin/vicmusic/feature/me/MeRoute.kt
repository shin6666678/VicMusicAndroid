package com.shin.vicmusic.feature.me

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.feature.auth.navigateToLogin
import com.shin.vicmusic.feature.checkIn.navigateToCheckIn
import com.shin.vicmusic.feature.common.bar.BarActionItem
import com.shin.vicmusic.feature.common.bar.BarTabItem
import com.shin.vicmusic.feature.common.bar.UniversalTopBar
import com.shin.vicmusic.feature.liked.navigateToLikedList
import com.shin.vicmusic.feature.localMusic.navigateToLocalMusic
import com.shin.vicmusic.feature.main.MainViewModel
import com.shin.vicmusic.feature.main.TopLevelDestination
import com.shin.vicmusic.feature.me.component.PlaylistsSection
import com.shin.vicmusic.feature.me.component.RecentBar
import com.shin.vicmusic.feature.me.component.UserInfoCard
import com.shin.vicmusic.feature.me.recentPlay.navigateToRecentPlay
import com.shin.vicmusic.feature.me.setting.navigateToSetting
import com.shin.vicmusic.feature.message.navigateToMessageList
import com.shin.vicmusic.feature.myInfo.navigateToMyInfo
import com.shin.vicmusic.feature.playlist.detail.navigateToPlaylistDetail
import com.shin.vicmusic.feature.playlist.meList.navigateToMyPlaylists
import com.shin.vicmusic.feature.relationship.RelationshipTab
import com.shin.vicmusic.feature.relationship.navigateToRelationship
import com.shin.vicmusic.feature.vip.navigateToVip
import com.shin.vicmusic.feature.me.dressup.navigateToDressUp
import com.shin.vicmusic.feature.me.audio.navigateToAudio
import com.shin.vicmusic.feature.me.purchased.navigateToPurchased
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.blur
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import com.shin.vicmusic.core.design.theme.LocalAppColors
import com.shin.vicmusic.core.design.theme.AppBackground


@Composable
fun MeRoute(
    viewModel: MeViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity),
) {
    val navController = LocalNavController.current

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val playlists by viewModel.myPlaylists.collectAsState()
    val unreadCount by mainViewModel.unreadCount.collectAsState()

    // 如果已登录但无用户信息，尝试获取
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == true) {
            if (currentUser == null) {
                viewModel.fetchUserInfo()
            }
            viewModel.fetchMyPlaylists()
        }
    }

    val topBarTabs = listOf(
        BarTabItem(
            name = "我的",
            isSelected = true,
            onClick = {}
        )
    )

    val topBarActions = listOf(
        BarActionItem(
            icon = Icons.Default.MailOutline,
            contentNumber = unreadCount,
            onClick = { navController.navigateToMessageList() }
        ),
        BarActionItem(
            icon = Icons.Filled.Tune,
            onClick = { navController.navigateToSetting() }
        )
    )

    MeScreen(
        topBarTabs = topBarTabs,
        topBarActions = topBarActions,

        onAvatarClick = navController::navigateToMyInfo,
        onLoginClick = navController::navigateToLogin,
        isLoggedIn = isLoggedIn ?: false,
        user = currentUser,
        myPlaylists = playlists,
        onMorePlaylistsClick = { navController.navigateToMyPlaylists() },

        onFollowClick = { navController.navigateToRelationship(RelationshipTab.FOLLOWING) },
        onFansClick = { navController.navigateToRelationship(RelationshipTab.FAN) },
        onLevelClick = { navController.navigateToMyInfo() },
        onHeardClick = { navController.navigateToRecentPlay() },

        onFriendClick = { navController.navigateToRelationship(RelationshipTab.FRIEND) },
        onVipClick = navController::navigateToVip,
        onCheckInClick = { navController.navigateToCheckIn() },
        onDressUpClick = { navController.navigateToDressUp() },

        onLikedClick = { navController.navigateToLikedList() },
        onLocalClick = { navController.navigateToLocalMusic() },

        onAudioClick = { navController.navigateToAudio() },
        onPurchasedClick = { navController.navigateToPurchased() },

        recentPlayList = playlists,
        recentNum = 2,
        recentIcon = playlists.firstOrNull()?.cover ?: "",
        onRecentOrMoreClick = navController::navigateToRecentPlay,

        onPlayListClick = navController::navigateToPlaylistDetail,

        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen(
    topBarTabs: List<BarTabItem>,
    topBarActions: List<BarActionItem>,

    onLoginClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onVipClick: () -> Unit = {},
    onLikedClick: () -> Unit = {},
    onLocalClick: () -> Unit = {},
    onAudioClick: () -> Unit = {},
    onPurchasedClick: () -> Unit = {},
    isLoggedIn: Boolean,
    user: UserInfo? = null,
    onMorePlaylistsClick: () -> Unit = {},
    myPlaylists: List<Playlist> = emptyList(), // 歌单列表

    recentPlayList: List<Playlist> = emptyList(),
    recentNum: Int = 0,                      // 最近播放歌曲数量
    recentIcon: String = "",                 // 最近播放歌曲封面
    onRecentOrMoreClick: () -> Unit = {},    // 点击"全部已播"或"更多"

    onFollowClick: () -> Unit = {},
    onFansClick: () -> Unit = {},
    onLevelClick: () -> Unit = {},
    onHeardClick: () -> Unit = {},

    onFriendClick: () -> Unit = {},
    onCheckInClick: () -> Unit = {},
    onDressUpClick: () -> Unit = {},

    onPlayListClick: (String) -> Unit = {}
) {
    val appColors = LocalAppColors.current
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            UniversalTopBar(
                tabs = topBarTabs,
                actions = topBarActions,
                contentColor = appColors.textColor
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // User Info Card
            Spacer(Modifier.height(16.dp))
            UserInfoCard(
                onLoginClick = onLoginClick,
                onAvatarClick = onAvatarClick,
                onVipClick = onVipClick,
                isLoggedIn = isLoggedIn,
                user = user,
                onFollowClick = onFollowClick,
                onFansClick = onFansClick,
                onLevelClick = onLevelClick,
                onHeardClick = onHeardClick,
                onFriendClick = onFriendClick,
                onCheckInClick = onCheckInClick,
                onDressUpClick = onDressUpClick
            )

            // Quick Access Icons
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickAccessItem(
                    icon = Icons.Filled.Favorite,
                    text = "收藏",
                    count = "2",
                    onClick = onLikedClick
                )
                QuickAccessItem(
                    icon = Icons.Filled.Download,
                    text = "本地",
                    count = "29",
                    onClick = onLocalClick
                )
                QuickAccessItem(
                    icon = Icons.Filled.Headphones,
                    text = "有声",
                    count = "0",
                    onClick = onAudioClick
                )
                QuickAccessItem(
                    icon = Icons.Filled.ReceiptLong,
                    text = "已购",
                    count = "0",
                    onClick = onPurchasedClick
                )
            }

            // Recently Played Section
            RecentBar(
                playlists = recentPlayList,
                recentNum = recentNum,
                recentIcon = recentIcon,
                onRecentOrMoreClick = onRecentOrMoreClick,
            )

            // Song Lists Section
            Spacer(Modifier.height(24.dp))
            PlaylistsSection(
                playlists = myPlaylists,
                onPlaylistClick = onPlayListClick,
                onMorePlaylistsClick = onMorePlaylistsClick
            )

            Spacer(Modifier.height(100.dp))
        }

    }
}

@Composable
fun QuickAccessItem(
    icon: ImageVector,
    text: String,
    count: String,
    onClick: () -> Unit = {}
) {
    val textColor = LocalAppColors.current.textColor

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(28.dp),
            tint = textColor
        )
        Spacer(Modifier.height(4.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = textColor)
        Text(
            text = count,
            style = MaterialTheme.typography.labelSmall,
            color = textColor.copy(alpha = 0.6f)
        )
    }
}

@Preview
@Composable
fun MeScreenPreview() {
    MeScreen(
        // 预览时需要提供假数据
        topBarTabs = listOf(BarTabItem("我的", true, {})),
        topBarActions = listOf(
            BarActionItem(Icons.Default.Search, onClick = {}),
            BarActionItem(Icons.Default.Menu, onClick = {})
        ),
        isLoggedIn = false
    )
}