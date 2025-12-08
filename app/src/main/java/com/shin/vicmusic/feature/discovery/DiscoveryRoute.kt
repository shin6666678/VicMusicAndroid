package com.shin.vicmusic.feature.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController // 导入 NavController
import com.shin.vicmusic.R
import com.shin.vicmusic.core.design.theme.LocalDividerColor
import com.shin.vicmusic.core.design.theme.SpaceExtraMedium
import com.shin.vicmusic.core.design.theme.SpaceOuter
import com.shin.vicmusic.core.model.Song
import com.shin.vicmusic.feature.player.PlayerViewModel
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterProvider
import com.shin.vicmusic.feature.song.ItemSong
import com.shin.vicmusic.feature.song.navigateToSongDetail
import com.shin.vicmusic.util.getPlayerViewModelSingleton



@Composable
fun DiscoveryRoute(
    navController: NavController,
    viewModel: DiscoveryViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = getPlayerViewModelSingleton()
) {
    val datum by viewModel.datum.collectAsState()
    DiscoveryScreen(
        songs = datum,
        toggleDrawer = {}, // 保持原有的空实现，或者替换为实际逻辑
        toSearch = { navController.navigate("search_route") }, // 点击搜索框时导航到搜索界面
        onSongClick = { songId -> navController.navigateToSongDetail(songId) } ,
        onAddToQueueClick = { song -> playerViewModel.addSongToQueue(song) }
    )
}

@Composable
fun DiscoveryScreen(
    toggleDrawer: () -> Unit = {},
    toSearch: () -> Unit = {},
    songs: List<Song> = listOf(),
    onSongClick: (String) -> Unit = {} ,
    onAddToQueueClick: (Song) -> Unit = {} 
) {
    Scaffold(
        topBar = {
            DiscoveryTopBar(toggleDrawer, toSearch)
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(paddingValues)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = SpaceOuter),
                verticalArrangement = Arrangement.spacedBy(SpaceExtraMedium),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(songs) { song ->
                    ItemSong(
                        data = song,
                        modifier = Modifier.clickable { onSongClick(song.id) } ,
                        onAddToQueueClick = { onAddToQueueClick(song) }
                    )
                }
            }
        }
    }
}


/**
 * 发现顶部标题栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryTopBar(toggleDrawer: () -> Unit, toSearch: () -> Unit) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = toggleDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(LocalDividerColor.current)
                    .clickable { toSearch() } // 点击时调用 toSearch 回调
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "搜索框在这",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        actions = {
            Icon(
                painter = painterResource(id = R.drawable.ic_video_comment),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(horizontal = SpaceExtraMedium)
                    .size(20.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Preview
@Composable
fun PreView(
    @PreviewParameter(DiscoveryPreviewParameterProvider::class)
    songs:List<Song>
) {
    DiscoveryScreen(songs = songs)
}