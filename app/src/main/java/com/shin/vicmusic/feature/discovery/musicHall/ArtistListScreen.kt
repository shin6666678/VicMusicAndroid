package com.shin.vicmusic.feature.discovery.musicHall

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.ARTISTS
import com.shin.vicmusic.feature.discovery.DiscoveryViewModel
@Preview
@Composable
fun ArtistListScreenPreview() {
    ArtistListScreen(navController = NavController(LocalContext.current))
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistListScreen(
    navController: NavController,
    viewModel: DiscoveryViewModel = hiltViewModel()
) {
    // 模拟数据，实际应该从 ViewModel 获取
    // 这里使用预览数据
    val artists = ARTISTS

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "歌手",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Search */ }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // 常听歌手/关注歌手 区域
            TopArtistSection()

            // 筛选区域
            FilterSection()

            // 歌手列表
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(artists) { artist ->
                    ArtistListItem(artist = artist)
                }
            }
        }
    }
}

@Composable
fun TopArtistSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "常听歌手",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* TODO: View All Followed */ }
            ) {
                Text(
                    text = "关注歌手",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(3) { index ->
                TopArtistCard(index)
            }
        }
    }
}

@Composable
fun TopArtistCard(index: Int) {
    // 模拟数据
    val names = listOf("林俊杰", "G.E.M. 邓...", "王力宏")
    val images = listOf(
        "https://example.com/jj.jpg",
        "https://example.com/gem.jpg",
        "https://example.com/leehom.jpg"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp)
            // 添加阴影效果
            .run {
               // 这里可以添加 shadow modifier，简单起见省略
               this
            }

    ) {
        AsyncImage(
            model = images[index],
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = names[index],
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        // 播放按钮图标 (此处简化为一个简单的圆形播放按钮)
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun FilterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        FilterRow(listOf("全部", "内地", "港台", "欧美", "日本", "韩国"))
        FilterRow(listOf("全部", "男", "女", "组合"))
        FilterRow(listOf("全部", "流行", "说唱", "国风", "摇滚", "电子"))
    }
}

@Composable
fun FilterRow(options: List<String>) {
    var selectedOption by remember { mutableStateOf(options[0]) }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        items(options) { option ->
            val isSelected = option == selectedOption
            FilterChip(
                selected = isSelected,
                onClick = { selectedOption = option },
                label = { Text(text = option) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1DB954), // 类似 Spotify 绿或者图中的绿色
                    selectedLabelColor = Color.White,
                    containerColor = Color.Transparent,
                    labelColor = Color.Black
                ),
                border = null, // 去掉边框
                shape = RoundedCornerShape(50) // 圆角
            )
        }
    }
}

@Composable
fun ArtistListItem(artist: Artist) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Navigate to Artist Detail */ }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = artist.image,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = artist.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        // 关注按钮
        if (artist.isFollowing) {
             OutlinedButton(
                onClick = { /* TODO: Unfollow */ },
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp),
                 border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.LightGray))

            ) {
                Text(
                    text = "已关注",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        } else {
             OutlinedButton(
                onClick = { /* TODO: Follow */ },
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp),
                 border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.Gray))
            ) {
                 Icon(
                     imageVector = Icons.Default.Add,
                     contentDescription = null,
                     modifier = Modifier.size(12.dp),
                     tint = Color.Black
                 )
                Text(
                    text = "关注",
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
        }
    }
}
