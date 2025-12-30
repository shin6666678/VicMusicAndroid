package com.shin.vicmusic.feature.common.icon

import android.R.attr.level
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
@Composable
fun PlayCountIcon(
    playCount:Int
){
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.LightGray)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = "${playCount} Times", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
    }
}