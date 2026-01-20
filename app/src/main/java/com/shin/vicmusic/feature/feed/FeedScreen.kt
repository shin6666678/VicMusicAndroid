package com.shin.vicmusic.feature.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.domain.Feed
import com.shin.vicmusic.feature.feed.component.FeedItemCard

@Composable
fun FeedScreen(
    feedItems: List<Feed>,
    onProfileClick: (String) -> Unit,
    onSongClick: (String) -> Unit,
    onPlaylistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onPlayClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = feedItems,
            key = { feed -> feed.id }
        ) { feed ->
            FeedItemCard(
                feed = feed,
                modifier = Modifier.padding(horizontal = 8.dp),
                onProfileClick = onProfileClick,
                onSongClick = onSongClick,
                onPlaylistClick = onPlaylistClick,
                onAlbumClick = onAlbumClick,
                onLikeClick = onLikeClick,
                onCommentClick = onCommentClick,
                onPlayClick = onPlayClick
            )
        }
    }
}
