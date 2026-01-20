package com.shin.vicmusic.feature.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController

@Composable
fun FeedRoute(
    viewModel: FeedViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val feedItems by viewModel.feedItems.collectAsState()

    FeedScreen(
        feedItems = feedItems,
        onProfileClick = { /* TODO: Navigate to profile screen */ },
        onSongClick = { /* TODO: Navigate to song detail */ },
        onPlaylistClick = { /* TODO: Navigate to playlist detail */ },
        onAlbumClick = { /* TODO: Navigate to album detail */ },
        onLikeClick = { /* TODO: Handle like action */ },
        onCommentClick = { /* TODO: Navigate to comment screen */ },
        onPlayClick = { /* TODO: Handle play action */ },
    )
}
