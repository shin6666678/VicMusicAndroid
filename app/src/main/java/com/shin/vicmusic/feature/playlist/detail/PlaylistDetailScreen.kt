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
import com.shin.vicmusic.feature.common.ItemSong
import com.shin.vicmusic.feature.playlist.detail.component.PlaySongActionHeader
import com.shin.vicmusic.feature.playlist.detail.component.PlaylistHeader

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
            onChangePublicStatus = viewModel::changePublicStatus
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
    onChangePublicStatus:(String)->Unit={}
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

            item {
                PlaylistHeader(detail)
            }

            item {
                PlaySongActionHeader(
                    playListId = detail.info.id,
                    songCount = detail.info.songCount,
                    onChangePublicStatus=onChangePublicStatus
                )
            }

            itemsIndexed(detail.songs) { index, song ->
                ItemSong(
                    song = song,
                )
            }
        }
    }
}
