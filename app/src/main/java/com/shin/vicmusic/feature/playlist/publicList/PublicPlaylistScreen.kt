package com.shin.vicmusic.feature.playlist.publicList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.PLAYLISTS
import com.shin.vicmusic.feature.common.bar.CommonTopBar
import com.shin.vicmusic.feature.common.ItemPlaylist
import com.shin.vicmusic.feature.playlist.detail.navigateToPlaylistDetail

@Preview
@Composable
fun PreView() {
    PublicPlaylistScreen(
        playlists = PLAYLISTS,
        onPlaylistClick = {}
    )
}

@Composable
fun PublicPlaylistRoute(
    viewModel: PublicPlaylistViewModel = hiltViewModel()
) {
    val playlists by viewModel.playlists.collectAsState()
    val navController = LocalNavController.current
    LaunchedEffect(Unit) {
        viewModel.fetchPublicPlaylists()
    }

    PublicPlaylistScreen(
        playlists = playlists,
        onPlaylistClick = navController::navigateToPlaylistDetail,
        popBackClick = navController::popBackStack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicPlaylistScreen(
    playlists: List<Playlist>,
    onPlaylistClick: (String) -> Unit,
    popBackClick: () -> Unit = {},
) {

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                CommonTopBar(
                    midText = "公开歌单",
                    popBackStack = popBackClick
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "共有 ${playlists.size} 张公开歌单 ", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.List, contentDescription = "List")
                    }
                }
            }
            items(playlists) { playlist ->
                ItemPlaylist(playlist = playlist, onClick = { onPlaylistClick(playlist.id) })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}