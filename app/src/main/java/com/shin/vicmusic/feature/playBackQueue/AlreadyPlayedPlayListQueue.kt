package com.shin.vicmusic.feature.playBackQueue

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AlreadyPlayedPlayListQueue (){
    // --- 已播歌单 (Placeholder) ---
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            "暂未开发已播歌单记录",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}