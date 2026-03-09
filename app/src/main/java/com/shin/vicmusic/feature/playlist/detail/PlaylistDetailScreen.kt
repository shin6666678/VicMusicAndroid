package com.shin.vicmusic.feature.playlist.detail

import android.graphics.drawable.BitmapDrawable
import android.os.UserManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.model.UiState
import com.shin.vicmusic.feature.common.DetailControllerBar
import com.shin.vicmusic.feature.common.bar.BarActionItem
import com.shin.vicmusic.feature.common.bar.BarTabItem
import com.shin.vicmusic.feature.common.bar.CommonTopBarSelect
import com.shin.vicmusic.feature.common.item.ItemSongNumbered
import com.shin.vicmusic.feature.playlist.PlaylistShareCard
import com.shin.vicmusic.feature.playlist.detail.component.PlaylistHeader
import com.shin.vicmusic.feature.playlist.sheet.PlaylistSettingSheet
import com.shin.vicmusic.feature.playlist.sheet.PlaylistShareActionSheet
import com.shin.vicmusic.util.QRCodeUtils
import com.shin.vicmusic.util.ResourceUtil
import com.shin.vicmusic.util.ShareUtils
import com.shin.vicmusic.util.captureComposable
import com.shin.vicmusic.util.copyUriToCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PlaylistDetailRoute(
    viewModel: PlaylistDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    // 图片选择启动器
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val localPath = copyUriToCache(context, it)
            if (localPath != null) {
                viewModel.onNewCoverSelected(localPath)
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    PlaylistDetailScreen(
        uiState = uiState,
        onBackClick = navController::popBackStack,
        onRemoveSong = viewModel::removeSongFromPlaylist,
        onPublicStatusChange = { newStatus -> viewModel.onPublicStatusChange(newStatus) },
        onCollectClick = viewModel::toggleCollect,
        onPlayAllClick = { /* 以后对接播放器 */ },
        onShareToFeed = { playlistId ->
            navController.navigate("publish_feed?targetId=$playlistId&targetType=playlist")
        },
        onNameChange = viewModel::onNameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onCoverClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        },
        saveChanges = viewModel::saveChanges
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    uiState: UiState<PlayListDetailUiState>,
    onBackClick: () -> Unit,
    onRemoveSong: (String) -> Unit,
    onPublicStatusChange: (Int) -> Unit,
    onCollectClick: () -> Unit,
    onPlayAllClick: () -> Unit,
    onShareToFeed: (String) -> Unit,

    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCoverClick: () -> Unit,
    saveChanges: () -> Unit,

    ) {

    val detail = uiState.data.detail
    val info = detail.info
    val songs = detail.songs
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
                        val shareLandingUrl =
                            "http://115.190.155.131:9001/share_playlist.html?id=${detail.info.id}"
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
    var showSettingSheet by remember { mutableStateOf(false) }
    if (showSettingSheet) {
        PlaylistSettingSheet(
            info = uiState.data.detail.info,
            onDismissRequest = { showSettingSheet = false },
            onNameChange = { onNameChange(it) },
            onDescriptionChange = { onDescriptionChange(it) },
            onCoverClick = { onCoverClick() },
            onPublicStatusChange = { onPublicStatusChange(it) },
            onSaveClick = { saveChanges() }
        )
    }

    Scaffold(
        topBar = {
            CommonTopBarSelect(
                onBackClick = onBackClick,
                tabs = listOf(
                    BarTabItem(
                        isSelected = true,
                        name = detail.info.name,
                        onClick = {}
                    ),
                ),
                actions =
                    listOf(
                        BarActionItem(
                            icon = Icons.Default.Share,
                            contentDescription = "分享",
                            onClick = { showShareSheet = true }
                        ),
                        BarActionItem(
                            icon = Icons.Default.Settings,
                            contentDescription = "设置",
                            onClick = { showSettingSheet = true }
                        )
                    ),
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

            itemsIndexed(detail.songs) { index, song ->
                ItemSongNumbered(
                    song = song,
                    num = index,
                    showDeleteFromPlaylist = true,
                    onDeleteClick = { onRemoveSong(song.id) }
                )
            }
        }
    }
}