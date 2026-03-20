package com.shin.vicmusic.feature.common.item

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONG

@Preview(showBackground = true)
@Composable
fun ItemSongNumberedPreview() {
    ItemSongNumbered(
        num = 0,
        song = SONG
    )
}

@Composable
fun ItemSongNumbered(
    song: Song,
    num: Int? = null,
    isRank: Boolean = false,
    showPlayCount: Boolean = false,
    showDeleteFromPlaylist: Boolean = false,
    onClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {

    val text = num?.let { (it + 1).toString() } ?: "-"
    var rankColor = Color.Gray
    if (isRank && num != null && num < 3)
        rankColor = Color.Red

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = rankColor,
            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
        )

        ItemSong(
            song = song,
            modifier = Modifier.weight(1f),
            showPlayCount = showPlayCount,
            showDeleteFromPlaylist = showDeleteFromPlaylist,
            onClick = onClick,
            onDeleteClick = onDeleteClick
        )
    }
}