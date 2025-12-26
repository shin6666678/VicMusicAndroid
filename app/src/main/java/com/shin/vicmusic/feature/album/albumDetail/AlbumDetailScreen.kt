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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.feature.common.CommonTopBar
import com.shin.vicmusic.feature.common.MyAsyncImage
import com.shin.vicmusic.feature.common.ItemSong

@Composable
fun AlbumDetailRoute(
    viewModel: AlbumDetailViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()
    AlbumDetailScreen(
        uiState = uiState,
        popBackStack = navController::popBackStack,
    )
}

@Composable
fun AlbumDetailScreen(
    uiState: AlbumDetailUiState,
    popBackStack: () -> Unit,
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
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = uiState.album?.title ?: "未知专辑",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }

                    // Song List
                    itemsIndexed(uiState.songs) { index, song ->
                        ItemSong(
                            song = song,
                        )
                    }
                }
            }
        }
    }
}