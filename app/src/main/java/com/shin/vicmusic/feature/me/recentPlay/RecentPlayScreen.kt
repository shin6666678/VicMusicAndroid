package com.shin.vicmusic.feature.me.recentPlay

import androidx.compose.foundation.layout.Box
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.feature.common.CommonTopBar
import com.shin.vicmusic.feature.common.ItemSong

@Composable
fun RecentPlayScreen(
    viewModel: RecentPlayViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = { CommonTopBar("最近播放") }
    ) { padding ->
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(contentPadding = padding) {
                items(viewModel.songList) { song ->
                    ItemSong(
                        song = song,
                    )
                }
            }
        }
    }
}