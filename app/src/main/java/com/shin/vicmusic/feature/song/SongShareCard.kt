package com.shin.vicmusic.feature.song

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.R
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Song

/**
 * 品牌分享卡片：海报/邀请函风格
 * * 设计规范：
 * 1. 最外层直角，防止分享到第三方平台时圆角边缘留白。
 * 2. 封面大圆角 + 悬浮阴影，增加海报精致感。
 * 3. 标题使用品牌主色调。
 * 4. 右下角二维码区域预留。
 */
@Composable
fun SongShareCard(
    song: Song,
    albumArtBitmap: Bitmap?,
    qrCodeBitmap: Bitmap? = null // 二维码位图，由外部生成后传入
) {
    // 品牌色调：Spotify 风格的绿色，可以根据应用实际配置调整
    val brandGreen = Color(0xFF1DB954)

    Column(
        modifier = Modifier
            .size(320.dp, 480.dp) // 固定比例海报尺寸
            .background(Color.White)
            .border(0.5.dp, Color.LightGray.copy(alpha = 0.5f)) // 极细边框防止在纯白背景下消失
    ) {
        // --- 1. 顶部 Header：品牌标识 ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "VICMUSIC",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "值得与你共享的旋律",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                letterSpacing = 3.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- 2. 中间部分：大封面展示 ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF5F5F5),
                shadowElevation = 10.dp // 增加阴影深度
            ) {
                if (albumArtBitmap != null) {
                    Image(
                        bitmap = albumArtBitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "VicMusic",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- 3. 歌曲内容与二维码区 ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp, vertical = 28.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // 标题颜色与应用色调契合
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = brandGreen,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = song.artist.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "来自 VicMusic 客户端",
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
            }

            // 右下角二维码显示区
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(Color.White)
                        .border(0.5.dp, Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (qrCodeBitmap != null) {
                        Image(
                            bitmap = qrCodeBitmap.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // 占位预览逻辑
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            alpha = 0.2f
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "扫码识别",
                    fontSize = 8.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

/**
 * 分享卡片实时预览
 */
@Preview(showBackground = true, backgroundColor = 0xFFEEEEEE)
@Composable
fun SongShareCardPreview() {
    val mockSong = Song(
        id = "1",
        title = "七里香",
        artist = Artist(id = "1", name = "周杰伦"),
        icon = ""
    )
    MaterialTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            SongShareCard(
                song = mockSong,
                albumArtBitmap = null // 预览时封面为空显示占位
            )
        }
    }
}