package com.shin.vicmusic.feature.feed.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.domain.ArtistUpdate
import com.shin.vicmusic.core.domain.Feed
import com.shin.vicmusic.core.domain.ShareableContent
import com.shin.vicmusic.core.domain.SystemRecommendation
import com.shin.vicmusic.core.domain.UserActivity
import com.shin.vicmusic.core.domain.UserPost

@Composable
fun FeedItemCard(
    feed: Feed,
    modifier: Modifier = Modifier,
    onProfileClick: (String) -> Unit = {},
    onSongClick: (String) -> Unit = {},
    onPlaylistClick: (String) -> Unit = {},
    onAlbumClick: (String) -> Unit = {},
    onLikeClick: (String) -> Unit = {},
    onCommentClick: (String) -> Unit = {},
    onPlayClick: (String) -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            // 1. Header Section
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                val author: Any
                val actionText: String

                when (feed) {
                    is UserPost -> {
                        author = feed.user
                        actionText = "分享了动态"
                        FeedItemHeader(
                            author = author,
                            timestamp = feed.timestamp,
                            actionText = actionText,
                            onProfileClick = { onProfileClick(feed.user.id) }
                        )
                    }
                    is UserActivity -> {
                        author = feed.user
                        actionText = feed.activityType.description
                        FeedItemHeader(
                            author = author,
                            timestamp = feed.timestamp,
                            actionText = actionText,
                            onProfileClick = { onProfileClick(feed.user.id) }
                        )
                    }
                    is ArtistUpdate -> {
                        author = feed.artist
                        actionText = "发布了新作品"
                        FeedItemHeader(
                            author = author,
                            timestamp = feed.timestamp,
                            actionText = actionText,
                            onProfileClick = { onProfileClick(feed.artist.id) }
                        )
                    }
                    is SystemRecommendation -> {
                        Text(text = "为你推荐", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Body Section
            FeedItemBody(
                feed = feed,
                onSongClick = onSongClick,
                onPlaylistClick = onPlaylistClick,
                onAlbumClick = onAlbumClick
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Footer Section
            var likeCount = 0
            var commentCount = 0
            var showPlayButton = false
            var contentId = ""

            when (feed) {
                is UserPost -> {
                    when(val content = feed.content) {
                        is ShareableContent.SharedSong -> {
                            likeCount = content.song.likesCount
                            commentCount = content.song.commentsCount
                            showPlayButton = true
                            contentId = content.song.id
                        }
                        is ShareableContent.SharedPlaylist -> {
                            likeCount = content.playlist.likeCount
                            contentId = content.playlist.id
                        }
                    }
                }
                is UserActivity -> {
                     when(val content = feed.content) {
                        is ShareableContent.SharedSong -> {
                            likeCount = content.song.likesCount
                            commentCount = content.song.commentsCount
                            showPlayButton = true
                            contentId = content.song.id
                        }
                        is ShareableContent.SharedPlaylist -> {
                            likeCount = content.playlist.likeCount
                            contentId = content.playlist.id
                        }
                    }
                }
                is ArtistUpdate -> {
                    // Album has no like/comment counts in the domain model
                    contentId = feed.album.id
                }
                is SystemRecommendation -> {
                    likeCount = feed.playlist.likeCount
                    contentId = feed.playlist.id
                }
            }

            FeedItemFooter(
                likeCount = likeCount,
                commentCount = commentCount,
                showPlayButton = showPlayButton,
                modifier = Modifier.padding(horizontal = 8.dp),
                onLikeClick = { onLikeClick(contentId) },
                onCommentClick = { onCommentClick(contentId) },
                onPlayClick = { onPlayClick(contentId) }
            )
        }
    }
}
