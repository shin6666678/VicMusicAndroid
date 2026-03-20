package com.shin.vicmusic.feature.song.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.theme.LocalAppColors
import com.shin.vicmusic.core.manager.PlayerUiState
import com.shin.vicmusic.util.TimeUtil.formatTime

@Composable
fun PlaybackControlBar(
    playerUiState: PlayerUiState,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDragging = remember { mutableStateOf(false) }
    val draggedPosition = remember { mutableFloatStateOf(0f) }

    val sliderPosition = if (isDragging.value) {
        draggedPosition.floatValue
    } else {
        playerUiState.currentPosition.toFloat()
    }

    val bufferProgress = if (playerUiState.duration > 0) {
        (playerUiState.bufferedPosition.toFloat() / playerUiState.duration.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val currentFormattedTime = formatTime(playerUiState.currentPosition)
    val totalFormattedTime = formatTime(playerUiState.duration)

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {

        // 使用 Box 进行层叠布局
        Box(contentAlignment = Alignment.Center) {

            // 层级 1: 缓冲进度条 (显示在底层)
            LinearProgressIndicator(
                progress = { bufferProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp) // 匹配 Slider 轨道的视觉高度
                    .padding(horizontal = 0.dp), // 视觉微调，根据需要调整
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f), // 缓冲颜色：半透明的次要色
                trackColor = MaterialTheme.colorScheme.surfaceVariant, // 背景底色
                strokeCap = StrokeCap.Round
            )

            // 层级 2: 播放进度条 Slider (显示在顶层)
            Slider(
                value = sliderPosition,
                onValueChange = { newValue ->
                    isDragging.value = true
                    draggedPosition.floatValue = newValue
                },
                onValueChangeFinished = {
                    onSeek(draggedPosition.floatValue.toLong())
                    isDragging.value = false
                },
                valueRange = 0f..playerUiState.duration.coerceAtLeast(0).toFloat(),
                enabled = playerUiState.duration > 0,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    //将未播放的轨道颜色设为透明，以便能看到底下的缓冲条
                    inactiveTrackColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = currentFormattedTime,
                style = MaterialTheme.typography.labelSmall,
                color = LocalAppColors.current.songDetailTextEnd
            )
            Text(
                text = totalFormattedTime,
                style = MaterialTheme.typography.labelSmall,
                color = LocalAppColors.current.songDetailTextEnd
            )
        }
    }
}