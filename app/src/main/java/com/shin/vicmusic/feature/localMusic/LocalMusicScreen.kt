package com.shin.vicmusic.feature.localMusic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.feature.common.ItemSong
import com.shin.vicmusic.feature.common.bar.CommonTopBar

@Composable
fun LocalMusicRoute(
    viewModel: LocalMusicViewModel = hiltViewModel()
){
    val navController= LocalNavController.current
    val localSongs by viewModel.localSongs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.scanLocalMusic()
    }
    LocalMusicScreen(
        onBackClick = navController::popBackStack,
        localSongs = localSongs
    )
}
@Composable
fun LocalMusicScreen(
    onBackClick: () -> Unit,
    localSongs: List<Song>,
) {

    Scaffold(
        topBar = {
            CommonTopBar(
                midText = "本地音乐",
                popBackStack =  onBackClick
            )
        }
    ) {

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
                        // 由于 ItemSong 内部会注入 PlaylistViewModel，
                        // 为了避免不必要的耦合和开销，这里可以传入 null 或进行相应调整
                        // 如果 ItemSong 的 "添加到歌单" 等功能对本地音乐无用，
                        // 可以在 ItemSong 中增加逻辑判断来隐藏相关按钮。
                        // 这里我们暂时使用默认实现。
                        viewModel = null
                    )
                }
            }
        }
    }
}
