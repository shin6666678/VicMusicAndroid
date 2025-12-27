package com.shin.vicmusic.feature.me.recentPlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.feature.common.CommonTopBar
import com.shin.vicmusic.feature.common.ItemSong
@Preview
@Composable
fun RecentPlayScreenPreview() {
    RecentPlayScreen()
}
@Composable
fun RecentPlayRoute(
    viewModel: RecentPlayViewModel = hiltViewModel()
){
    val navController = LocalNavController.current
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    Scaffold(
        topBar = { CommonTopBar(midText = "最近播放", popBackStack = { navController.popBackStack()}) }
    ) { padding ->
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            RecentPlayScreen(viewModel.songList)
        }
    }
}

@Composable
fun RecentPlayScreen(
    songList: List<Song> = emptyList(),
    padding: PaddingValues = PaddingValues(0.dp)
) {

    LazyColumn(contentPadding = padding) {
        items(songList) { song ->
            ItemSong(
                song = song,
            )
        }
    }

}