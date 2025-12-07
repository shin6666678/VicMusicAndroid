package com.shin.vicmusic.feature.song.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.feature.player.PlayerState
import com.shin.vicmusic.util.formatTime

@Composable
fun PlaybackControlBar(
    playerState: PlayerState,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDragging = remember { mutableStateOf(false) }
    val draggedPosition = remember { mutableStateOf(0f) }

    val sliderPosition = if (isDragging.value) {
        draggedPosition.value
    } else {
        playerState.currentPosition.toFloat()
    }

    val currentFormattedTime = formatTime(playerState.currentPosition)
    val totalFormattedTime = formatTime(playerState.duration)

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Slider(
            value = sliderPosition,
            onValueChange = { newValue ->
                isDragging.value = true
                draggedPosition.value = newValue
            },
            onValueChangeFinished = {
                onSeek(draggedPosition.value.toLong())
                isDragging.value = false
            },
            valueRange = 0f..playerState.duration.toFloat(),
            enabled = playerState.duration > 0
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = currentFormattedTime, style = MaterialTheme.typography.labelSmall)
            Text(text = totalFormattedTime, style = MaterialTheme.typography.labelSmall)
        }
    }
}