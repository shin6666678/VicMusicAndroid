package com.shin.vicmusic.feature.playlist.daily

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.design.theme.LocalAppColors
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.UiState
import com.shin.vicmusic.feature.common.DetailControllerBar
import com.shin.vicmusic.feature.common.bar.CommonTopBarSelect
import com.shin.vicmusic.feature.common.item.ItemSongNumbered
import com.shin.vicmusic.feature.playlist.detail.component.PlaylistHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyPlaylistRoute(
    viewModel: DailyPlaylistViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val playerManager = LocalPlayerManager.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DailyPlaylistScreen(
        uiState = uiState,
        onBackClick = navController::popBackStack,
        onPlayAllClick = {
            val songs = uiState.data.detail.songs
            if (songs.isNotEmpty()) {
                playerManager.playSong(songs.first(), songs)
            }
        },
        onSongClick = { song ->
            val songs = uiState.data.detail.songs
            playerManager.playSong(song, songs)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyPlaylistScreen(
    uiState: UiState<DailyPlaylistUiState>,
    onBackClick: () -> Unit,
    onPlayAllClick: () -> Unit,
    onSongClick: (Song) -> Unit,
) {
    Scaffold(
        topBar = {
            CommonTopBarSelect(
                onBackClick = onBackClick,
                contentColor = LocalAppColors.current.songDetailIconColor
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.errorMessage)
                }
            }
            else -> {
                val detail = uiState.data.detail
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    item {
                        PlaylistHeader(detail = detail)
                    }

                    item {
                        DetailControllerBar(
                            songCount = detail.songs.size,
                            isLiked = false,
                            onPlayAllClick = onPlayAllClick,
                            onCollectClick = {}
                        )
                    }

                    itemsIndexed(detail.songs) { index, song ->
                        ItemSongNumbered(
                            song = song,
                            num = index,
                            onClick = { onSongClick(song) }
                        )
                    }
                }
            }
        }
    }
}
