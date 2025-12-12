package com.shin.vicmusic.feature.discovery.musicHall

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shin.vicmusic.core.design.theme.SpaceExtraMedium
import com.shin.vicmusic.core.design.theme.SpaceOuter
import com.shin.vicmusic.core.model.Song
import com.shin.vicmusic.feature.song.ItemSong


@Composable
fun MusicHall(
    songs:List<Song>,
    onSongClick: (String) -> Unit = {},
    onAddToQueueClick: (Song) -> Unit = {},
    // [新增] 传递回调
    onLikeClick: (Song) -> Unit = {}
){
    LazyColumn(
        contentPadding = PaddingValues(horizontal = SpaceOuter),
        verticalArrangement = Arrangement.spacedBy(SpaceExtraMedium),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(songs) { song ->
            ItemSong(
                data = song,
                modifier = Modifier.clickable { onSongClick(song.id) } ,
                onAddToQueueClick = { onAddToQueueClick(song) },
                onLikeClick = onLikeClick
            )
        }
    }
}