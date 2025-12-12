package com.shin.vicmusic.feature.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
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
import com.shin.vicmusic.core.model.Song
import com.shin.vicmusic.feature.player.PlayerViewModel
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterProvider
import com.shin.vicmusic.feature.song.navigateToSongDetail
import com.shin.vicmusic.util.getPlayerViewModelSingleton
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


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
    // 0 for 推荐, 1 for 乐馆
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            DiscoveryTopBar(
                toSearch = toSearch,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index -> selectedTabIndex = index }
            )
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
            if(selectedTabIndex == 0){
                Recommend()
            }
            else if (selectedTabIndex == 1) {
                MusicHall(
                    songs = songs,
                    onSongClick = onSongClick,
                    onAddToQueueClick = onAddToQueueClick
                )
            }
        }
    }
}

@Preview
@Composable
fun PreView(
    @PreviewParameter(DiscoveryPreviewParameterProvider::class)
    songs:List<Song>
) {
    DiscoveryScreen(songs = songs)
}