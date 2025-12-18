package com.shin.vicmusic.feature.artist.artistDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.shin.vicmusic.R
import com.shin.vicmusic.core.design.component.MyAsyncImage
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONGS
import com.shin.vicmusic.feature.song.ItemSong

@Preview
@Composable
fun ArtistDetailScreenPreview() {
    ArtistDetailScreen(
        artist = Artist(
            id = "1",
            name = "周杰伦",
            image = "https://www.facebook.com/jay/",
            description = "周杰伦，中国台湾流行乐男歌手、演员，1976年9月18日生于台湾省彰化县，现居台湾省彰化县。",
            followerCount = 1000000,
            isFollowing = true,
            region = "中国",
            type = "男",
            style = "民谣"
        ),
        songs = SONGS
    )
}

@Composable
fun ArtistDetailRoute(
    viewModel: ArtistDetailViewModel = hiltViewModel(),
) {
    // 观察歌手对象(Observe Artist object)
    val artist by viewModel.artist.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val navController = LocalNavController.current
    ArtistDetailScreen(
        artist = artist,
        songs = songs,
        onBackClick = navController::popBackStack,
    )
}

@Composable
fun ArtistDetailScreen(
    artist: Artist?,
    songs:List<Song>?,
    onBackClick: () -> Unit={},
    onFollowClick: (String) -> Unit={}
) {

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. 背景大图 (Background Image)
        if (artist != null) {
            MyAsyncImage(
                model = artist.image,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                contentScale = ContentScale.Crop,
            )
            // 渐变蒙层，保证文字清晰 (Gradient Scrim)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
        }

        // 2. 列表内容 (List Content)
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部留白，让出图片区域 (Spacer for header)
            item {
                Spacer(modifier = Modifier.height(260.dp))
            }

            // 信息卡片头部 (Info Card Header)
            item {
                if (artist != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        // 顶部装饰条 (Handle Bar)
                        Box(
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .size(40.dp, 4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.LightGray.copy(alpha = 0.4f))
                                .align(Alignment.CenterHorizontally)
                        )

                        // 歌手名称和关注按钮 (Name and Follow Button)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = artist.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "单曲 ${songs?.size}", // Song count
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                            Button(
                                onClick = { onFollowClick(artist.id) },
                                shape = RoundedCornerShape(50),
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(text = "+ 关注") // Follow
                            }
                        }

                        // 播放全部栏 (Play All Bar)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* Play All Logic */ }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_play_without_circle),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "播放热门歌曲", // Play hot songs
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = " (共${songs?.size}首)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Color.LightGray.copy(alpha = 0.2f)
                        )
                    }
                }
            }
            if(songs != null){
                // 歌曲列表 (Song List)
                itemsIndexed(songs) { index, song ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        ItemSong(
                            song = song,
                        )
                    }
                }
            }

            // 底部垫高，防止被播放器遮挡 (Bottom Spacer)
            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(MaterialTheme.colorScheme.background)
                )
            }
        }

        // 3. 顶部导航栏 (Top Bar)
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu_more),
                    contentDescription = "More",
                    tint = Color.White
                )
            }
        }
    }
}