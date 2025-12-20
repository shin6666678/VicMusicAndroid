package com.shin.vicmusic.feature.rankList.rankList.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class RecommendedRankListInfo(
    val title: String,
    val backgroundColor: Color
)

@Composable
fun RecommendedRankListItem(
    modifier: Modifier = Modifier,
    rankListInfo: RecommendedRankListInfo
) {
    Box(
        modifier = modifier
            .size(150.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(rankListInfo.backgroundColor)
            .padding(8.dp)
    ) {
        Text(
            text = rankListInfo.title,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.TopStart)
        )
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play",
            tint = Color.White,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

@Preview
@Composable
private fun RecommendedRankListItemPreview() {
    RecommendedRankListItem(
        rankListInfo = RecommendedRankListInfo(
            title = "腾讯音乐榜",
            backgroundColor = Color(0xFF42A5F5) // Blue
        )
    )
}
