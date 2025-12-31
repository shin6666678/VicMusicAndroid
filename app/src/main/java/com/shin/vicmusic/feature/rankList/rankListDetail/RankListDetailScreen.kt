package com.shin.vicmusic.feature.rankList.rankListDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.feature.rankList.rankListDetail.component.RankListDetailHeader
import com.shin.vicmusic.feature.rankList.rankListDetail.component.SongListSection

@Composable
fun RankListDetailRoute(
    navController: NavController = LocalNavController.current,
    viewModel: RankListDetailViewModel = hiltViewModel()
){

    val rankListDetail by viewModel.rankListDetail.collectAsState()

    // 1. 判空处理 (Null Check)
    if (rankListDetail == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator() // 显示加载中 (Loading)
        }
    } else {
        // [修改] 从 items 中筛选 Song，并提取标题和封面
        val data = rankListDetail!!
        val songList = data.items.filterIsInstance<Song>()

        RankListDetailScreen(
            title = data.title,       // [新增] 传递标题
            coverUrl = data.imageUrl, // [新增] 传递封面
            songs = songList,
            popBackStack = { navController.popBackStack() },
        )
    }
}
@Composable
fun RankListDetailScreen(
    title: String = "排行榜(Rank List)", // [新增] 参数
    coverUrl: String = "",            // [新增] 参数
    songs: List<Song> = emptyList(),
    popBackStack: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // [修改] 传递数据给 Header
        RankListDetailHeader(
            title = title,
            coverUrl = coverUrl,
            popBackStack = popBackStack
        )

        SongListSection(
            songs = songs,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRankListDetailScreen() {
    RankListDetailScreen()
}
