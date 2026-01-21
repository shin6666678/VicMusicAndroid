package com.shin.vicmusic.feature.me.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shin.vicmusic.R
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.feature.common.MyAsyncImage
import com.shin.vicmusic.feature.common.icon.UserLevelIcon
import com.shin.vicmusic.feature.common.icon.VipIcon

@Preview
@Composable
fun UserInfoCardPreview() {
    UserInfoCard(
        onAvatarClick = {},
        isLoggedIn = false,
        user = UserInfo(
            name = "登录情况测试用户",
            vipLevel = 0,
        )
    )
}

@Composable
fun UserInfoCard(
    onLoginClick: () -> Unit={},
    onAvatarClick: () -> Unit,
    onVipClick: () -> Unit = {},
    isLoggedIn: Boolean,
    user: UserInfo?,

    onFollowClick: () -> Unit = {},
    onFansClick: () -> Unit = {},
    onLevelClick:() -> Unit = {},
    onHeardClick: () -> Unit = {},

    onFriendClick: () -> Unit = {},
    onCheckInClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // 显式设置背景色，防止透明
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. 头部区域：根据登录状态切换
            if (isLoggedIn && user != null) {
                LoggedInHeader(onAvatarClick, onVipClick, user)
            } else {
                LoggedOutHeader(onLoginClick)
            }

            // 2. 统计数据区域 (仅登录显示)
            if (isLoggedIn && user != null) {
                Spacer(modifier = Modifier.height(20.dp))
                UserStatsRow(
                    user = user,
                    onFollowClick = onFollowClick,
                    onFansClick = onFansClick,
                    onLevelClick = onLevelClick,
                    onHeardClick = onHeardClick,
                    onCheckInClick = onCheckInClick,
                    onVipClick = onVipClick,
                    onFriendClick = onFriendClick
                )
            }
        }
    }
}

// --- 抽取的子组件 ---

@Composable
private fun LoggedInHeader(
    onAvatarClick: () -> Unit,
    onVipClick: () -> Unit, // [新增] 參數
    user: UserInfo
) {
    // 1. 解析 VIP 等级 (默认为 0)
    val vipLevelInt = user.vipLevel


    Row(verticalAlignment = Alignment.CenterVertically) {
        MyAsyncImage(
            model = user.headImg,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .clickable { onAvatarClick() },
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(16.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row {
                UserLevelIcon(level = user.level)
                Spacer(modifier = Modifier.width(8.dp))
                // VIP 标签
                VipIcon(
                    vipLevelInt=vipLevelInt,
                    onClick = onVipClick
                )
            }
        }
    }
}

@Composable
private fun LoggedOutHeader(onLoginClick: () -> Unit) {
    val VdGreen = Color(0xFF1AAD19)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 按钮：移除 weight，让它根据内容自适应宽度
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .height(40.dp), // 保持高度，移除 weight(0.5f)
            colors = ButtonDefaults.buttonColors(
                containerColor = VdGreen,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(50),
            // 2. 显式设置内边距，防止默认边距过大挤压文字
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 0.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Key,
                contentDescription = null,
                modifier = Modifier.size(18.dp) // 稍微调小图标以防万一
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "立即登录",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1 // 强制单行
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 3. 提示文字：使用 weight(1f) 填满剩余空间
        Text(
            text = "登录体验更多精彩内容",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.weight(1f) // 占据剩下的所有宽度
        )
    }
}

@Composable
private fun UserStatsRow(
    user: UserInfo,
    onFollowClick: () -> Unit = {},
    onFansClick: () -> Unit = {},
    onLevelClick:() -> Unit = {},
    onHeardClick: () -> Unit = {},


    onFriendClick: () -> Unit,
    onVipClick: () -> Unit = {},
    onCheckInClick: () -> Unit = {}
    
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        // 为关注添加点击事件
        StatItem(user.followCount, "关注", onClick = onFollowClick)
        StatItem(user.followerCount, "粉丝", onClick = onFansClick)
        StatItem(user.level, "等级", onClick = onLevelClick)
        StatItem(user.heardCount, "听歌", onClick = onHeardClick)
    }

    Spacer(Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        ActionItem(
            icon = Icons.Filled.People,
            text = "好友",
            iconTint = Color(0xFFFF9800),
            onClick = onFriendClick
        )
        ActionItem(icon = Icons.Filled.Favorite,
            text = "会员",
            iconTint = Color(0xFF00BFA5),
            onClick = onVipClick
        )
        ActionItem(icon = Icons.Filled.ShoppingCart, text = "装扮", iconTint = Color(0xFF2196F3))
        ActionItem(icon = Icons.Filled.DateRange,
            text = "日签",
            iconTint = Color(0xFFF44336),
            onClick = onCheckInClick
        )
    }
}

@Composable
fun StatItem(count: Int?, label: String, onClick: () -> Unit = {}) { // 添加 onClick 参数
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() } // 绑定点击
    ) {
        Text(
            text = (count ?: 0).toString(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}
@Composable
fun ActionItem(
    icon: ImageVector,
    text: String,
    iconTint: Color,
    onClick: () -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(24.dp),
            tint = iconTint
        )
        Spacer(Modifier.height(4.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}