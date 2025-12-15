package com.shin.vicmusic.feature.discovery.TestList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.design.theme.SpaceExtraMedium
import com.shin.vicmusic.core.design.theme.SpaceOuter
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.feature.song.ItemSong

@Composable
fun TestList(
    songs:List<Song>,
    onLikeClick: (Song) -> Unit = {}
){
    val playerManager = LocalPlayerManager.current

    LazyColumn(
        contentPadding = PaddingValues(horizontal = SpaceOuter),
        verticalArrangement = Arrangement.spacedBy(SpaceExtraMedium),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(songs) { song ->
            ItemSong(
                song = song,
                modifier = Modifier.clickable { playerManager.playSong(song)} ,
                onAddToQueueClick = { playerManager.addSongToQueue(song) },
                onLikeClick = onLikeClick
            )
        }
    }
}