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
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.feature.rankList.rankListDetail.component.RankListDetailHeader
import com.shin.vicmusic.feature.rankList.rankListDetail.component.SongListSection

@Composable
fun RankListDetailRoute(
    navController: NavController = LocalNavController.current,
    viewModel: RankListDetailViewModel = hiltViewModel()
){

    val playerManager = LocalPlayerManager.current

    val rankListDetail by viewModel.rankListDetail.collectAsState()
    // 1. 判空处理 (Null Check)
    if (rankListDetail == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator() // 显示加载中 (Loading)
        }
    } else {
        val songList = rankListDetail!!.items.filterIsInstance<Song>()
        val artistList = rankListDetail!!.items.filterIsInstance<Artist>()
        val albumList = rankListDetail!!.items.filterIsInstance<Album>()

        RankListDetailScreen(
            songs = songList,
            artists = artistList,
            albums = albumList,
            popBackStack = { navController.popBackStack() },
            onSongClick = playerManager::playSong,
        )
    }
}
@Composable
fun RankListDetailScreen(
    songs: List<Song> = emptyList() ,
    artists: List<Artist> = emptyList(),
    albums: List<Album> = emptyList(),
    popBackStack: () -> Unit = {},
    onSongClick: (Song) -> Unit = {},
    onPlayAllClick: (Artist) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        RankListDetailHeader()
        SongListSection(
            songs=songs,
            onSongClick = onSongClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRankListDetailScreen() {
    RankListDetailScreen()
}
