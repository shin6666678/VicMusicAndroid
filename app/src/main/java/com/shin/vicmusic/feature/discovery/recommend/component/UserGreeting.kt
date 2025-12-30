package com.shin.vicmusic.feature.discovery.recommend.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.feature.common.MyAsyncImage
import com.shin.vicmusic.core.design.theme.SpaceExtraMedium
import com.shin.vicmusic.core.design.theme.SpaceMedium
import com.shin.vicmusic.core.design.theme.SpaceOuter
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.feature.common.icon.UserLevelIcon
import com.shin.vicmusic.feature.common.icon.VipIcon
import com.shin.vicmusic.util.ResourceUtil

@Composable
fun UserGreeting(
    user: UserInfo?=null
) {

    val vipLevelInt = user?.vipLevel?: 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpaceOuter, vertical = SpaceMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // [修改] 使用 MyAsyncImage 加载网络头像
        MyAsyncImage(
            model = ResourceUtil.r2(user?.headImg?:""), // 传入头像 URL
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.width(SpaceMedium))
        Text(
            text = user?.name ?: "未登录", // 如果 user 为 null 显示默认文本
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(SpaceExtraMedium))
        // VIP6 Badge - Simplified for now
        if(user!=null){
            VipIcon(
               vipLevelInt=vipLevelInt
            )
            Spacer(modifier = Modifier.width(SpaceExtraMedium))
            // Another badge - Simplified
            UserLevelIcon(user.level)
        }
        Spacer(modifier = Modifier.weight(1f)) // Pushes content to the right
        Text(
            text = "1条新消息 >",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserGreeting() {
    VicMusicTheme {
        UserGreeting(
            UserInfo(
                name = "发现界面测试用户",
                vipLevel = 6
            )
        )
    }
}