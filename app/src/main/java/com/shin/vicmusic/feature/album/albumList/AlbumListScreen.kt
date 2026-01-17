package com.shin.vicmusic.feature.album.albumList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.ALBUMS
import com.shin.vicmusic.feature.album.albumDetail.navigateToAlbumDetail
import com.shin.vicmusic.feature.common.item.ItemAlbumSquare
import com.shin.vicmusic.feature.common.bar.CommonTopBar


@Composable
@Preview
fun AlbumListScreenPreview() {
    AlbumListScreen(AlbumListUiState.Success(ALBUMS))
}

@Composable
fun AlbumListRoute(
    viewModel: AlbumListViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()
    AlbumListScreen(
        uiState = uiState,
        popBackStack = navController::popBackStack,
        onAlbumClick = navController::navigateToAlbumDetail
    )
}

@Composable
fun AlbumListScreen(
    uiState: AlbumListUiState,
    popBackStack: () -> Unit = {},
    onAlbumClick: (String) -> Unit = {}
) {
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
                midText = "专辑",
                popBackStack = popBackStack,
                containerColor = Color.Transparent
            )

            // 使用 Box + weight 占据剩余空间，并处理内部居中逻辑
            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                when (val state = uiState) {
                    is AlbumListUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is AlbumListUiState.Error -> {
                        Text(text = state.message, modifier = Modifier.align(Alignment.Center))
                    }

                    is AlbumListUiState.Success -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize().padding(8.dp)
                        ) {
                            items(state.albums) { album ->
                                ItemAlbumSquare(album,onAlbumClick)
                            }
                        }
                    }
                }
            }
        }
    }
}