package com.shin.vicmusic.feature.discovery.recommend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shin.vicmusic.core.design.theme.SpaceExtraMedium
import com.shin.vicmusic.core.design.theme.SpaceOuter
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.feature.discovery.recommend.component.AlsoListeningSection
import com.shin.vicmusic.feature.discovery.recommend.component.HorizontalMediaCards
import com.shin.vicmusic.feature.discovery.recommend.component.RecommendSongItem
import com.shin.vicmusic.feature.discovery.recommend.component.UserGreeting

// Placeholder Data Classes
data class MediaCardData(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val isDaily: Boolean = false
)

data class RecommendSongData(
    val songId: String,
    val coverUrl: String,
    val title: String,
    val artist: String,
    val playCount: String? = null,
    val isPlaying: Boolean = false // For the current playing bar
)

@Composable
fun Recommend(
    onSongClick: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onMediaCardClick: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = SpaceOuter), // Add some bottom padding to avoid overlap with bottom navigation
        verticalArrangement = Arrangement.spacedBy(SpaceExtraMedium)
    ) {

        item {
            UserGreeting()
        }
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
        item {
            AlsoListeningSection(
                title = "听「梁静茹」的也在听",
                onPlayClick = { /* Handle play click */ },
                onCloseClick = { /* Handle close click */ }
            )
        }
        items(
            listOf(
                RecommendSongData(
                    songId = "s1",
                    coverUrl = "https://picsum.photos/80/80?random=1",
                    title = "爱协",
                    artist = "蔡依林-花蝴蝶",
                    playCount = "1个好友关注歌手"
                ),
                RecommendSongData(
                    songId = "s2",
                    coverUrl = "https://picsum.photos/80/80?random=2",
                    title = "再见",
                    artist = "G.E.M. 邓紫棋-新的心跳",
                    playCount = "7k人在听"
                ),
                RecommendSongData(
                    songId = "s3",
                    coverUrl = "https://picsum.photos/80/80?random=3",
                    title = "手心的蔷薇",
                    artist = "林俊杰-新地球",
                    playCount = "570w+"
                ),
                RecommendSongData(
                    songId = "s4",
                    coverUrl = "https://picsum.photos/80/80?random=4",
                    title = "约定",
                    artist = "周蕙-周蕙精选",
                    playCount = "昨日热播"
                )
            )
        ) { song ->
            RecommendSongItem(
                data = song,
                modifier = Modifier.clickable { onSongClick(song.songId) }
            )
        }
        // TODO: Add the current playing bar at the bottom if needed.
        // For now, it's out of the scope of this specific `RecommendedPage` composable
        // as it looks like a global player component.
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecommendedPage() {
    VicMusicTheme {
        Recommend()
    }
}








