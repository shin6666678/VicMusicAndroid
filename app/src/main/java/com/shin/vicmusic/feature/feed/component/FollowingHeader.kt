package com.shin.vicmusic.feature.feed.component

import androidx.compose.foundation.clickable // 导入
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.feature.common.MyAsyncImage

@Composable
fun FollowingHeader(
    user: UserInfo,
    backgroundImageUrl: String,
    onBgClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        // 背景图
        MyAsyncImage(
            model = backgroundImageUrl,
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onBgClick), // 添加点击事件
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(12.dp))
            MyAsyncImage(
                model = user.headImg,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
            )
        }
    }
}