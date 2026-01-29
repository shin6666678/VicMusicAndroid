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
import com.shin.vicmusic.feature.common.DetailControllerBar
import com.shin.vicmusic.feature.common.item.ItemSongNumbered
import com.shin.vicmusic.feature.common.bar.CommonTopBar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.shin.vicmusic.feature.playlist.PlaylistShareActionSheet
import com.shin.vicmusic.util.ShareUtils
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
            onChangePublicStatus = viewModel::changePublicStatus,
            onRemoveSong = { songId -> viewModel.removeSongFromPlaylist(songId) },
            onCollectClick = viewModel::toggleCollect,
            onPlayAllClick = {},
            onShareToFeed = { playlistId ->
                navController.navigate("publish_feed?targetId=$playlistId&targetType=playlist")
            }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("加载中...")
        }
    }
}

@Composable
fun PlaylistDetailScreen(
    detail: PlaylistDetail,
    onBackClick: () -> Unit,
    onChangePublicStatus: (String) -> Unit = {},
    onRemoveSong: (String) -> Unit = {},
    onCollectClick: () -> Unit = {},
    onPlayAllClick: () -> Unit = {},
    onShareToFeed: (String) -> Unit = {}
) {
    var showShareSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // 截图/生成图片相关的 State
    // 这里简化处理，实际可以使用 CaptureController 等库来生成图片
    // 也可以将 PlaylistShareCard 绘制到 Bitmap
    
    // FIXME: 这是一个简化实现，实际项目可能需要 View 截图或 Canvas 绘制
    // 这里我们暂时只做系统分享 (Text Link) 和 分享到动态 (Internal)
    
    if (showShareSheet) {
        PlaylistShareActionSheet(
            playlist = detail.info,
            onDismissRequest = { showShareSheet = false },
            onShareToFeedClick = {
                 onShareToFeed(detail.info.id)
            },
            onGenerateCardClick = {
                // TODO: 生成海报逻辑
                // ShareUtils.sharePlaylist(context, detail.info, bitmap)
            },
            onSystemShareClick = {
                ShareUtils.sharePlaylist(context, detail.info)
            }
        )
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                midText = "歌单详情",
                popBackStack = onBackClick,
                onShareClick = { showShareSheet = true }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            item {
                PlaylistHeader(
                    detail = detail,
                )
            }

            // --- 操作栏区域：全部播放 + 收藏 ---
            item {
                DetailControllerBar(
                    songCount = detail.info.songCount,
                    isLiked = detail.info.isLiked,
                    onPlayAllClick = onPlayAllClick,
                    onCollectClick = onCollectClick
                )
            }

            item {
                PlaySongActionHeader(
                    playListId = detail.info.id,
                    songCount = detail.info.songCount,
                    onChangePublicStatus = onChangePublicStatus
                )
            }

            itemsIndexed(detail.songs) { index, song ->
                ItemSongNumbered(
                    song = song,
                    num = index + 1,
                    showDeleteFromPlaylist = true,
                    onDeleteClick = { onRemoveSong(song.id) }
                )
            }
        }
    }
}