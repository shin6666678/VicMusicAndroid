package com.shin.vicmusic.feature.me

import android.R.attr.bottom
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.User
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.feature.auth.navigateToLogin
import com.shin.vicmusic.feature.checkIn.navigateToCheckIn
import com.shin.vicmusic.feature.common.CreatePlaylistDialog
import com.shin.vicmusic.feature.liked.LikedScreen
import com.shin.vicmusic.feature.liked.navigateToLikedList
import com.shin.vicmusic.feature.me.component.MeTopBar
import com.shin.vicmusic.feature.me.component.RecentBar
import com.shin.vicmusic.feature.me.component.SongListsSection
import com.shin.vicmusic.feature.me.component.TopNotifyBar
import com.shin.vicmusic.feature.me.component.UserInfoCard
import com.shin.vicmusic.feature.me.fanList.navigateToFanList
import com.shin.vicmusic.feature.me.followList.navigateToFollowList
import com.shin.vicmusic.feature.me.recentPlay.navigateToRecentPlay
import com.shin.vicmusic.feature.me.setting.navigateToSetting
import com.shin.vicmusic.feature.myInfo.navigateToMyInfo
import com.shin.vicmusic.feature.playlist.meList.navigateToMyPlaylists
import com.shin.vicmusic.feature.vip.navigateToVip


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeRoute(
    viewModel: MeViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState() //  观察 currentUser
    val playlists by viewModel.myPlaylists.collectAsState() // 观察歌单列表

    // 如果已登录但无用户信息，尝试获取
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == true) {
            if (currentUser == null) {
                viewModel.fetchUserInfo()
            }
            viewModel.fetchMyPlaylists() // [新增] 获取歌单
        }
    }

    // 添加 onAvatarClick 参数
    MeScreen(
        onSettingsClick = navController::navigateToSetting,

        onAvatarClick = navController::navigateToMyInfo,
        onLoginClick=navController::navigateToLogin,
        onVipClick = navController::navigateToVip,
        isLoggedIn = isLoggedIn ?: false,//确保状态为非空Boolean，null时默认为false
        user = currentUser,
        myPlaylists = playlists,
        onMorePlaylistsClick = { navController.navigateToMyPlaylists() },
        onLikedClick = {navController.navigateToLikedList()},

        recentPlayList = playlists,
        recentNum = 2,
        recentIcon = playlists.firstOrNull()?.cover ?: "",
        onRecentOrMoreClick = navController::navigateToRecentPlay

        ,
        onFollowClick = { navController.navigateToFollowList() },
        onFansClick = { navController.navigateToFanList() },
        onLevelClick = { navController.navigateToMyInfo() },
        onHeardClick = { navController.navigateToRecentPlay() },
        onCheckInClick = { navController.navigateToCheckIn() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen(
    onDrawerClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},

    onLoginClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onVipClick: () -> Unit = {},
    onLikedClick: () -> Unit = {},
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
    onLevelClick:() -> Unit = {},
    onHeardClick: () -> Unit = {},

    onCheckInClick: () -> Unit = {}
) {


    Scaffold(
        topBar = {
            MeTopBar(
                onSettingsClick=onSettingsClick
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
                onCheckInClick = onCheckInClick
            )

            // Quick Access Icons
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // 传入 onClick 回调
                QuickAccessItem(
                    icon = Icons.Filled.Favorite,
                    text = "收藏",
                    count = "2",
                    onClick = onLikedClick
                )
                QuickAccessItem(icon = Icons.Filled.Download, text = "本地", count = "29")
                QuickAccessItem(icon = Icons.Filled.Headphones, text = "有声", count = "6")
                QuickAccessItem(icon = Icons.Filled.ReceiptLong, text = "已购", count = "1")
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
            SongListsSection(playlists = myPlaylists, onMorePlaylistsClick = onMorePlaylistsClick)

            Spacer(Modifier.height(100.dp))
        }

    }

}

@Composable
fun QuickAccessItem(
    icon: ImageVector,
    text: String,
    count: String,
    onClick: () -> Unit = {} // [新增] 默认为空
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(4.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall)
        Text(text = count, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}



@Preview
@Composable
fun MeScreenPreview() {
    MeScreen(
        isLoggedIn = false
    )
}
