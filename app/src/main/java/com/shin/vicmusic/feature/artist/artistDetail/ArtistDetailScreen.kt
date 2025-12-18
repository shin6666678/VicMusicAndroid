package com.shin.vicmusic.feature.artist.artistDetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.R
import com.shin.vicmusic.core.design.component.MyAsyncImage
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Artist

@Composable
fun ArtistDetailRoute(
    viewModel: ArtistDetailViewModel = hiltViewModel(),
) {
    // 观察歌手对象(Observe Artist object)
    val artist by viewModel.artist.collectAsState()
    val navController = LocalNavController.current
    ArtistDetailScreen(
        onBackClick = navController::popBackStack,
        artist = artist
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    onBackClick: () -> Unit,
    artist: Artist?
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("歌手详情(Artist Detail)") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "返回(Back)"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            artist?.let {
                // 加载成功显示内容(Show content on success)
                ArtistContent(artist = it)
            } ?: run {
                // 数据为空显示加载中(Show loading if data is null)
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun ArtistContent(artist: Artist) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // 歌手头像(Artist Avatar)
            MyAsyncImage(
                model = artist.image,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 歌手名字(Artist Name)
            Text(
                text = artist.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 歌手描述(Artist Description)
            Text(
                text = artist.description ?: "暂无介绍(No description)",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 关注按钮(Follow Button)
            Button(
                onClick = { /* 模拟关注(Mock Follow) */ },
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text("关注(Follow)")
            }
        }
    }
}