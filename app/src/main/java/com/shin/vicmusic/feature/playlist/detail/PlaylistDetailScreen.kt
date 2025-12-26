package com.shin.vicmusic.feature.playlist.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.PlaylistDetail
import com.shin.vicmusic.feature.common.CommonTopBar
import com.shin.vicmusic.feature.playlist.detail.component.PlaylistHeader
import com.shin.vicmusic.feature.common.ItemSong

@Composable
fun PlaylistDetailRoute(
    viewModel: PlaylistDetailViewModel = hiltViewModel()
) {
    val detail by viewModel.detail.collectAsState()
    val navController = LocalNavController.current

    if (detail != null) {
        PlaylistDetailScreen(
            detail = detail!!,
            onBackClick = navController::popBackStack,
        )
    } else {
        // Loading state
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("加载中...")
        }
    }
}

@Composable
fun PlaylistDetailScreen(
    detail: PlaylistDetail,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            CommonTopBar(midText = "歌单详情", popBackStack = onBackClick)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header 部分
            item {
                PlaylistHeader(detail)
            }

            // 歌曲列表部分
            itemsIndexed(detail.songs) { index, song ->
                ItemSong(
                    song = song,
                )
            }
        }
    }
}
