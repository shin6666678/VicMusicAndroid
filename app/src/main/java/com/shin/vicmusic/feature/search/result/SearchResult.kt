package com.shin.vicmusic.feature.search.result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.feature.common.ItemAlbum
import com.shin.vicmusic.feature.common.ItemArtist
import com.shin.vicmusic.feature.common.ItemPlaylist
import com.shin.vicmusic.feature.common.ItemSong
import com.shin.vicmusic.feature.common.ItemUser
import com.shin.vicmusic.feature.search.SearchTab
import com.shin.vicmusic.feature.search.SearchUiState

@Composable
fun SearchResultContent(
    uiState: SearchUiState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        if (uiState.selectedTab == SearchTab.COMPREHENSIVE) {
            uiState.comprehensiveResult?.let { result ->
                if (result.songs.isNotEmpty()) {
                    item { SectionTitle("单曲") }
                    items(result.songs) { song ->
                        ItemSong(song = song)
                    }
                }
                if (result.playlists.isNotEmpty()) {
                    item { SectionTitle("歌单") }
                    items(result.playlists) { playlist ->
                        // 修正参数名，假设为 playList (参考之前上传的 PlayList.kt 风格) 或 playlist
                        ItemPlaylist(playlist = playlist, onClick = {})
                    }
                }
                if (result.albums.isNotEmpty()) {
                    item { SectionTitle("专辑") }
                    items(result.albums) { album ->
                        ItemAlbum(
                            album = album,
                            onAlbumClick = {}
                        )
                    }
                }
                if (result.artists.isNotEmpty()) {
                    item { SectionTitle("歌手") }
                    items(result.artists) { artist ->
                        ItemArtist(artist = artist, onClick = {})
                    }
                }
                if (result.users.isNotEmpty()) {
                    item { SectionTitle("用户") }
                    items(result.users) { user ->
                        ItemUser(user = user)
                    }
                }

                if (result.songs.isEmpty() && result.playlists.isEmpty() && result.albums.isEmpty() && result.artists.isEmpty() && result.users.isEmpty()) {
                    item { EmptyState() }
                }
            }
        } else {
            // Specific Lists
            if (uiState.listResult.isEmpty()) {
                item { EmptyState() }
            } else {
                items(uiState.listResult) { item ->
                    when (item) {
                        is Song -> ItemSong(song = item)
                        is Playlist -> ItemPlaylist(playlist = item, onClick = {})
                        is Album -> ItemAlbum(
                            album = item,
                            onAlbumClick = {}
                        )
                        is Artist -> ItemArtist(artist = item, onClick = {})
                        is UserInfo -> ItemUser(user = item)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
        Text("未找到相关内容", color = Color.Gray)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}