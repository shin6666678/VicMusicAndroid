package com.shin.vicmusic.feature.album

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.R
import com.shin.vicmusic.core.domain.Album

/**
 * 专辑分享卡片
 * 设计风格：类似黑胶唱片的圆形裁剪或者经典的方形封面
 */
@Composable
fun AlbumShareCard(
    album: Album,
    coverBitmap: Bitmap?,
    qrCodeBitmap: Bitmap? = null
) {
    Column(
        modifier = Modifier
            .size(320.dp, 480.dp)
            .background(Color.White)
            .border(0.5.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        // --- 1. 顶部 Header ---
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
                text = "推荐一张好专辑",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                letterSpacing = 3.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- 2. 中间部分：专辑封面 (模拟黑胶或者CD盒效果) ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            // 背景黑胶盘片效果 (装饰)
            Box(
                 modifier = Modifier
                     .size(220.dp)
                     .offset(x = 20.dp) // 稍微向右偏移，露出碟片
                     .clip(CircleShape)
                     .background(Color.Black)
            )

            // 封面
            Surface(
                modifier = Modifier
                    .size(220.dp),
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFF5F5F5),
                shadowElevation = 12.dp
            ) {
                if (coverBitmap != null) {
                    Image(
                        bitmap = coverBitmap.asImageBitmap(),
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
                            "Album",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- 3. 专辑信息与二维码 ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp, vertical = 28.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = album.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = album.artist.name, // 使用 artist 对象或 artistName
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1DB954), // 歌手名高亮
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                if (album.releaseTime.isNotEmpty()) { // 修正字段名 publishTime -> releaseTime 或根据 Domain 定义
                     Text(
                        text = "发行时间：${album.releaseTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "来自 VicMusic 客户端",
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
            }

            // 二维码
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
                    text = "扫码试听",
                    fontSize = 8.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}
