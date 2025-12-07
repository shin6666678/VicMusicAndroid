package com.shin.vicmusic.feature.song.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shin.vicmusic.R

@Composable
fun RecordPlayerView(
    albumArtUrl: String?,
    isPlaying: Boolean=true,
    modifier: Modifier = Modifier,
    rotationSpeed: Int = 3000 // 可选：旋转速度（毫秒/圈），默认3秒一圈
) {
    // 保存当前旋转角度（暂停时冻结，播放时继续）
    var currentRotation by remember { mutableStateOf(0f) }

    // 动画核心：根据播放状态切换动画行为
    val rotationAngle by animateFloatAsState(
        // 播放时目标值=360f（无限循环旋转），暂停时目标值=当前角度（冻结）
        targetValue = if (isPlaying) 360f else currentRotation,
        animationSpec = if (isPlaying) {
            // 原生无限循环：匀速、每圈耗时固定，速度绝对一致
            infiniteRepeatable(
                animation = tween(
                    durationMillis = rotationSpeed,
                    easing = LinearEasing, // 强制匀速，无速度波动
                    delayMillis = 0
                ),
                repeatMode = RepeatMode.Restart // 每圈结束后无缝衔接下一圈
            )
        } else {
            // 暂停时：0毫秒动画，直接冻结当前角度
            tween(durationMillis = 0)

        },
        // 关键：实时保存当前旋转角度（包括动画过程中的中间值）
        finishedListener = { updatedAngle ->
            if (isPlaying) {
                // 播放时，动画每完成一圈（360f），重置currentRotation避免数值溢出
                currentRotation = updatedAngle % 360f
            } else {
                // 暂停时，保存最终冻结的角度
                currentRotation = updatedAngle
            }
        },
        label = "AlbumCoverRotation"
    )

    Box(modifier = modifier
        .size(300.dp) // 唱片机大致尺寸
        .clip(RoundedCornerShape(8.dp)) // 模拟唱片机外壳圆角
        .background(Color(0xFF333333)), // 唱片机底色
        contentAlignment = Alignment.Center
    ) {
        // 唱片盘（大黑圆）
        Box(modifier = Modifier
            .size(280.dp)
            .clip(CircleShape)
            .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // 专辑封面 (中间的圆)
            AsyncImage(
                model = albumArtUrl,
                contentDescription = "专辑封面",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(180.dp) // 专辑封面尺寸
                    .clip(CircleShape)
                    .rotate(rotationAngle), // 应用旋转角度
                // 优化：加载中/失败占位图（避免空白）
                placeholder = painterResource(id = R.drawable.ic_launcher), // 替换为你的默认封面
                error = painterResource(id = R.drawable.ic_launcher) // 加载失败时显示
            )
            // 唱片臂（简化）
            Image(painterResource(id = R.drawable.ic_fabulous), // 假设有唱片臂图片
                contentDescription = "唱片臂",
                modifier = Modifier
                    .size(100.dp, 150.dp) // 简化尺寸
                    .align(Alignment.TopEnd) // 定位到右上角
                    .offset(x = 30.dp, y = (-30).dp) // 微调位置
            )
        }
    }
}