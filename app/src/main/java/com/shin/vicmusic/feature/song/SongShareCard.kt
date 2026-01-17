package com.shin.vicmusic.feature.song

import android.R.attr.text
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shin.vicmusic.R
import com.shin.vicmusic.core.domain.Song

@Composable
fun SongShareCard(song: Song) {
    Column(
        modifier = Modifier
            .width(300.dp) // 固定宽度，方便截图
            .background(Color(0xFF2C2C2E), RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 示例：在 SongShareCard 中使用 Coil 的 AsyncImage
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(song.icon) // 专辑封面 URL
                .crossfade(true)
                .build(),
            contentDescription = "专辑封面",
            onSuccess = {
                // 图片加载成功
                Log.d("SongShareCard", "图片加载成功")
            },
            onError = { error ->
                // 图片加载失败
                Log.e("SongShareCard", "图片加载失败: ${error.result.throwable}")
            },
            // 当加载失败时，显示一个占位图
            error = painterResource(id = R.drawable.logo)
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = song.title,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = song.artist.name,
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "来自 VicMusic",
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}
