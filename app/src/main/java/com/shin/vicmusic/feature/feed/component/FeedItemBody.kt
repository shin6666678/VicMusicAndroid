package com.shin.vicmusic.feature.feed.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import com.shin.vicmusic.feature.common.item.ItemAlbum
import com.shin.vicmusic.feature.common.item.ItemPlaylist
import com.shin.vicmusic.feature.common.item.ItemSong
import com.shin.vicmusic.feature.common.item.ItemSongContent
import com.shin.vicmusic.feature.common.item.ItemText

@Composable
fun FeedItemBody(
    feed: Feed,
    modifier: Modifier = Modifier,
    onSongClick: (String) -> Unit = {},
    onPlaylistClick: (String) -> Unit = {},
    onAlbumClick: (String) -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        when (feed) {
            is UserPost -> {
                // 1. 如果有分享心得，则显示文字
                if (!feed.comment.isNullOrBlank()) {
                    ItemText(
                        text = feed.comment,
                    )
                }

                // 2. 显示分享的具体内容 (歌曲或歌单)
                when (val content = feed.content) {
                    is ShareableContent.SharedSong -> {
                        ItemSong(
                            song = content.song,
                        )
                    }
                    is ShareableContent.SharedPlaylist -> {
                        ItemPlaylist(
                            playlist = content.playlist,
                            onClick = { onPlaylistClick(content.playlist.id) }
                        )
                    }
                    is ShareableContent.SharedAlbum->{
                        ItemAlbum(
                            album = content.album,
                            onClick = { onAlbumClick(content.album.id) }
                        )
                    }
                    null -> {
                    }
                }
            }
            is UserActivity -> {
                // 显示用户活动涉及的内容 (歌曲或歌单)
                when (val content = feed.content) {
                    is ShareableContent.SharedSong -> {
                        ItemSongContent(
                            song = content.song,
                            onPlayClick = { onSongClick(content.song.id) }
                        )
                    }
                    is ShareableContent.SharedPlaylist -> {
                        ItemPlaylist(
                            playlist = content.playlist,
                            onClick = { onPlaylistClick(content.playlist.id) }
                        )
                    }
                    is ShareableContent.SharedAlbum->{
                        ItemAlbum(
                            album = content.album,
                            onClick = { onAlbumClick(content.album.id) }
                        )
                    }
                }
            }
            is ArtistUpdate -> {
                // 显示歌手发布的新专辑
                ItemAlbum(
                    album = feed.album,
                    onClick = { onAlbumClick(feed.album.id) }
                )
            }
            is SystemRecommendation -> {
                // 显示系统推荐的歌单
                ItemPlaylist(
                    playlist = feed.playlist,
                    onClick = { onPlaylistClick(feed.playlist.id) }
                )
            }
        }
    }
}
