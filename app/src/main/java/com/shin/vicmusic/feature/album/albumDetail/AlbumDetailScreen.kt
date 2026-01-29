package com.shin.vicmusic.feature.album.albumDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.feature.common.DetailControllerBar
import com.shin.vicmusic.feature.common.item.ItemSongNumbered
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.shin.vicmusic.feature.album.AlbumShareActionSheet
import com.shin.vicmusic.util.ShareUtils
import com.shin.vicmusic.feature.common.MyAsyncImage
import com.shin.vicmusic.feature.common.bar.CommonTopBar
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
import com.shin.vicmusic.feature.album.AlbumShareCard
import android.util.Log

@Composable
fun AlbumDetailRoute(
    viewModel: AlbumDetailViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()

    AlbumDetailScreen(
        uiState = uiState,
        popBackStack = navController::popBackStack,
        onLikeClick = viewModel::toggleLike,
        onPlayAllClick = { /* TODO: 绑定播放列表逻辑 */ },
        onShareToFeed = { albumId ->
             navController.navigate("publish_feed?targetId=$albumId&targetType=album")
        }
    )
}

@Composable
fun AlbumDetailScreen(
    uiState: AlbumDetailUiState,
    popBackStack: () -> Unit,
    onLikeClick: () -> Unit,
    onPlayAllClick: () -> Unit = {},
    onShareToFeed: (String) -> Unit = {}
) {
    var showShareSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val compositionContext = rememberCompositionContext()

    if (showShareSheet) {
        AlbumShareActionSheet(
            album = uiState.album!!,
            onDismissRequest = { showShareSheet = false },
            onShareToFeedClick = {
                uiState.album?.let { onShareToFeed(it.id) }
            },
            onGenerateCardClick = {
                // 生成海报逻辑
                coroutineScope.launch {
                    val tag = "ShareProcess"
                    try {
                        val album = uiState.album!!
                        // 1. 加载封面图 (IO 线程)
                        val coverBitmap = withContext(Dispatchers.IO) {
                            val loader = context.imageLoader
                            val request = ImageRequest.Builder(context)
                                .data(ResourceUtil.r2(album.icon))
                                .allowHardware(false)
                                .build()
                            (loader.execute(request) as? SuccessResult)?.let {
                                (it.drawable as BitmapDrawable).bitmap
                            }
                        }

                        // 2. 生成二维码
                        val shareLandingUrl = "http://115.190.155.131:9001/share_album.html?id=${album.id}"
                        val qrBitmap = withContext(Dispatchers.Default) {
                            QRCodeUtils.createQRCode(shareLandingUrl, 200)
                        }

                        // 3. 渲染并截图 (Main 线程)
                        val shareCardBitmap = withContext(Dispatchers.Main) {
                            captureComposable(context, compositionContext) {
                                AlbumShareCard(
                                    album = album,
                                    coverBitmap = coverBitmap,
                                    qrCodeBitmap = qrBitmap
                                )
                            }
                        }

                        // 4. 调用系统分享
                        ShareUtils.shareAlbum(context, album, shareCardBitmap)

                    } catch (e: Exception) {
                        Log.e(tag, "分享异常", e)
                    }
                }
            },
            onSystemShareClick = {
                uiState.album?.let { ShareUtils.shareAlbum(context, it) }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFB0C4DE), Color(0xFFE6E6FA))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CommonTopBar(
                midText = "专辑详情",
                popBackStack = popBackStack,
                containerColor = Color.Transparent,
                onShareClick = { if (uiState.album != null) showShareSheet = true }
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error, color = MaterialTheme.colorScheme.error)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Album Header
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MyAsyncImage(
                                model = uiState.album?.icon,
                                modifier = Modifier
                                    .size(200.dp)
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = uiState.album?.title ?: "未知专辑",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.Black.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Item 2: Controller Bar (Play All + Collect)
                    item {
                        // 使用通用组件，替代原有的硬编码按钮
                        DetailControllerBar(
                            songCount = uiState.songs.size,
                            isLiked = uiState.album?.isLiked == true,
                            onPlayAllClick = onPlayAllClick,
                            onCollectClick = onLikeClick
                        )
                    }

                    // Item 3: Song List
                    itemsIndexed(uiState.songs) { index, song ->
                        ItemSongNumbered(
                            song = song,
                            num = index + 1,
                        )
                    }
                }
            }
        }
    }
}