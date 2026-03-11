package com.shin.vicmusic.feature.song.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.theme.LocalAppColors
import com.shin.vicmusic.core.domain.Song

@Composable
fun SongInfoSection(song: Song, modifier: Modifier = Modifier, onLikeClick: () -> Unit = {}) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = song.title, // 歌曲标题
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), // 加粗标题
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = song.artist.name, // 曲信息
                style = MaterialTheme.typography.bodySmall,
                color = LocalAppColors.current.songDetailTextStart,
                modifier = Modifier.weight(1f)
            )
        }
        if (song.disclaimer.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = song.disclaimer,
                style = MaterialTheme.typography.labelSmall,
                color = LocalAppColors.current.songDetailTextEnd,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}