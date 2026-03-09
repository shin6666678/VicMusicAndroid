package com.shin.vicmusic.feature.song.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.core.domain.LyricLine

@Composable
fun LyricView(
    lyricList: List<LyricLine>,
    currentIndex: Int,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // 监听索引变化并执行平滑滚动(Smooth Scroll)
    LaunchedEffect(currentIndex) {
        if (currentIndex > 0) {
            // 滚动到当前行的上一行，保持视觉居中
            listState.animateScrollToItem((currentIndex - 1).coerceAtLeast(0))
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(lyricList) { index, line ->
            val isCurrent = index == currentIndex
            Text(
                text = line.content,
                modifier = Modifier.padding(vertical = 6.dp),
                color = if (isCurrent) Color.White else Color.White.copy(alpha = 0.5f),
                fontSize = if (isCurrent) 18.sp else 16.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}