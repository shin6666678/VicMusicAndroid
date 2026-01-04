package com.shin.vicmusic.feature.discovery.recommend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.design.theme.SpaceExtraMedium
import com.shin.vicmusic.core.design.theme.SpaceOuter
import com.shin.vicmusic.core.domain.RecommendCard
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.feature.discovery.recommend.component.AlsoListeningSection
import com.shin.vicmusic.feature.discovery.recommend.component.HorizontalMediaCards
import com.shin.vicmusic.feature.discovery.recommend.component.UserGreeting
import com.shin.vicmusic.feature.main.MainViewModel
import com.shin.vicmusic.feature.message.navigateToMessageList

// UI Data Classes
data class MediaCardData(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val isDaily: Boolean = false
)

// 这个数据类被 RecommendSongItem 引用
data class RecommendSongData(
    val songId: String,
    val coverUrl: String,
    val title: String,
    val artist: String,
    val playCount: String? = null,
    val isPlaying: Boolean = false
)
@Composable
fun RecommendRoute(
    viewModel: RecommendViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
){
    val navController = LocalNavController.current

    // 收集状态
    val user by viewModel.userInfo.collectAsState()
    val recommendCard by viewModel.recommendCard.collectAsState()
    val unreadCount by mainViewModel.unreadCount.collectAsState()

    RecommendScreen(
        user = user,
        recommendCard = recommendCard,
        unreadCount = unreadCount,
        onMessageClick = { navController.navigateToMessageList() },
        onMediaCardClick = {  }
    )
}

@Composable
fun RecommendScreen(
    user: UserInfo? = null,
    recommendCard: RecommendCard = RecommendCard(title = "", songs = emptyList()),
    unreadCount: Int = 0,
    onMediaCardClick: (String) -> Unit = {},
    onMessageClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = SpaceOuter),
        verticalArrangement = Arrangement.spacedBy(SpaceExtraMedium)
    ) {
        // 1. 用户问候区 (包含红点逻辑)
        item {
            UserGreeting(
                user = user,
                unreadCount = unreadCount,
                onMessageClick = onMessageClick
            )
        }

        // 2. 横向卡片区
        item {
            HorizontalMediaCards(
                mediaCards = listOf(
                    MediaCardData("1", "下午茶", "此刻别无所求...", "https://picsum.photos/200/300?random=1"),
                    MediaCardData("2", "Daily 30", "每日30首", "https://picsum.photos/200/300?random=2", true),
                    MediaCardData("3", "跑步专属", "节奏感超强", "https://picsum.photos/200/300?random=3")
                ),
                onMediaCardClick = onMediaCardClick
            )
        }

        // 3. "也在听" 区块
        item {
            AlsoListeningSection(
                title = recommendCard.title,
                songs = recommendCard.songs,
            )
        }
    }
}