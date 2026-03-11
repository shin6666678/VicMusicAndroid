package com.shin.vicmusic.feature.localMusic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.feature.common.bar.BarActionItem
import com.shin.vicmusic.feature.common.bar.BarTabItem
import com.shin.vicmusic.feature.common.item.ItemSong
import com.shin.vicmusic.feature.common.bar.CommonTopBar
import com.shin.vicmusic.feature.common.bar.CommonTopBarSelect
import kotlinx.coroutines.launch

@Composable
fun LocalMusicRoute(
    viewModel: LocalMusicViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val localSongs by viewModel.localSongs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.scanLocalMusic()
    }
    LocalMusicScreen(
        onBackClick = navController::popBackStack,
        localSongs = localSongs,
        onRefreshClick = viewModel::scanLocalMusic
    )
}

@Composable
fun LocalMusicScreen(
    onBackClick: () -> Unit,
    onRefreshClick: () -> Unit,
    localSongs: List<Song>,
) {

    val tabTitles = listOf("本地音乐")

    val topBarTabs = tabTitles.mapIndexed { index, title ->
        BarTabItem(
            name = title,
            isSelected = true,
            onClick = {}
        )
    }

    Scaffold(
        topBar = {
            CommonTopBarSelect(
                tabs = topBarTabs,
                onBackClick = onBackClick,
                actions = listOf(
                    BarActionItem(
                        icon = Icons.Default.Refresh,
                        contentDescription = "刷新",
                        onClick = onRefreshClick
                    ),
                ),
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (localSongs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("没有找到本地音乐 (｡•́︿•̀｡)")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(localSongs) { song ->
                        ItemSong(
                            song = song,
                            viewModel = null
                        )
                    }
                }
            }
        }

    }
}
