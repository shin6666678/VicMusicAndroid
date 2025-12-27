package com.shin.vicmusic.feature.discovery.recommend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shin.vicmusic.core.design.theme.SpaceExtraMedium
import com.shin.vicmusic.core.design.theme.SpaceOuter
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.core.domain.RecommendCard
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.feature.discovery.recommend.component.AlsoListeningSection
import com.shin.vicmusic.feature.discovery.recommend.component.HorizontalMediaCards
import com.shin.vicmusic.feature.discovery.recommend.component.UserGreeting

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
    user: UserInfo?=null,
    recommendCard: RecommendCard,
){
    RecommendScreen(
        user = user,
        recommendCard=recommendCard
    )
}
@Composable
fun RecommendScreen(
    user: UserInfo? = null,
    recommendCard: RecommendCard = RecommendCard(title = "", songs = emptyList()),
    onMediaCardClick: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = SpaceOuter),
        verticalArrangement = Arrangement.spacedBy(SpaceExtraMedium)
    ) {

        // 1. 用户问候区
        item {
            UserGreeting(user = user)
        }

        // 2. 横向卡片区 (这里可以保留静态或根据需求改为动态)
        item {
            HorizontalMediaCards(
                mediaCards = listOf(
                    MediaCardData(
                        id = "1",
                        title = "下午茶",
                        description = "此刻别无所求,只想...",
                        imageUrl = "https://picsum.photos/200/300?random=1"
                    ),
                    MediaCardData(
                        id = "2",
                        title = "Daily 30",
                        description = "每日30首",
                        imageUrl = "https://picsum.photos/200/300?random=2",
                        isDaily = true
                    ),
                    MediaCardData(
                        id = "3",
                        title = "跑步专属",
                        description = "节奏感超强",
                        imageUrl = "https://picsum.photos/200/300?random=3"
                    )
                ),
                onMediaCardClick = onMediaCardClick
            )
        }

        // 3. "也在听" 区块 (静态示例，可根据需求动态化)
        item {
            AlsoListeningSection(
                title = recommendCard.title,
                songs = recommendCard.songs,
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecommendedPage() {
    VicMusicTheme {
        // 预览时提供一些假数据
        RecommendScreen(

        )
    }
}