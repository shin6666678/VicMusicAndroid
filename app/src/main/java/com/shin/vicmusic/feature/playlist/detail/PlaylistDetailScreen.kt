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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberCompositionContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import android.graphics.drawable.BitmapDrawable
import com.shin.vicmusic.util.QRCodeUtils
import com.shin.vicmusic.util.ResourceUtil
import com.shin.vicmusic.util.captureComposable
import com.shin.vicmusic.feature.playlist.PlaylistShareCard
import android.util.Log

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
    
    val coroutineScope = rememberCoroutineScope()
    val compositionContext = rememberCompositionContext()
    
    if (showShareSheet) {
        PlaylistShareActionSheet(
            playlist = detail.info,
            onDismissRequest = { showShareSheet = false },
            onShareToFeedClick = {
                 onShareToFeed(detail.info.id)
            },
            onGenerateCardClick = {
                // 生成海报逻辑
                coroutineScope.launch {
                    val tag = "ShareProcess"
                    try {
                        // 1. 加载封面图 (IO 线程)
                        val coverBitmap = withContext(Dispatchers.IO) {
                            val loader = context.imageLoader
                            val request = ImageRequest.Builder(context)
                                .data(ResourceUtil.r2(detail.info.cover))
                                .allowHardware(false)
                                .build()
                            (loader.execute(request) as? SuccessResult)?.let {
                                (it.drawable as BitmapDrawable).bitmap
                            }
                        }

                        // 2. 生成二维码 (H5 落地页 URL)
                        // 假设落地页 URL 格式统一
                        val shareLandingUrl = "http://115.190.155.131:9001/share_playlist.html?id=${detail.info.id}"
                        val qrBitmap = withContext(Dispatchers.Default) {
                            QRCodeUtils.createQRCode(shareLandingUrl, 200)
                        }

                        // 3. 渲染并截图 (Main 线程)
                        val shareCardBitmap = withContext(Dispatchers.Main) {
                            captureComposable(context, compositionContext) {
                                PlaylistShareCard(
                                    playlist = detail.info,
                                    coverBitmap = coverBitmap,
                                    qrCodeBitmap = qrBitmap
                                )
                            }
                        }

                        // 4. 调用系统分享，带上生成的图片
                        ShareUtils.sharePlaylist(context, detail.info, shareCardBitmap)

                    } catch (e: Exception) {
                        Log.e(tag, "分享异常", e)
                    }
                }
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