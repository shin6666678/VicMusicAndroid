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
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.feature.me.ActionItem
import com.shin.vicmusic.feature.me.fanList.navigateToFanList
import com.shin.vicmusic.feature.me.followList.navigateToFollowList
import com.shin.vicmusic.feature.vip.VipIcon

@Preview
@Composable
fun UserInfoCardPreview() {
    UserInfoCard(
        onAvatarClick = {},
        isLoggedIn = true,
        user = UserInfo(
            name = "登录情况测试用户",
            vipLevel = 0,
        )
    )
}

@Composable
fun UserInfoCard(
    onAvatarClick: () -> Unit,
    onVipClick: () -> Unit = {}, // [新增] 接收 VIP 點擊事件
    isLoggedIn: Boolean,
    user: UserInfo?,
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
                LoggedOutHeader(onAvatarClick)
            }

            // 2. 统计数据区域 (仅登录显示)
            if (isLoggedIn && user != null) {
                Spacer(modifier = Modifier.height(20.dp))
                UserStatsRow(user = user)
            }

            // 3. 底部功能按钮区域 (使用循环减少代码)
            Spacer(Modifier.height(16.dp))
            ActionButtonsRow()
        }
    }
}

// --- 抽取的子组件 ---

@Composable
private fun LoggedInHeader(
    onAvatarClick: () -> Unit,
    onVipClick: () -> Unit, // [新增] 參數
    user: UserInfo?
) {
    // 1. 解析 VIP 等级 (默认为 0)
    val vipLevelInt = user?.vipLevel ?: -1

    // 2. 根据等级定义 UI 样式 (颜色和文字)
    // 这里是一个简单的示例逻辑，你可以根据需求调整颜色和等级划分
    val (vipTagText, vipBgColor, vipTextColor) = when {
        vipLevelInt >= 6 -> Triple("VIP $vipLevelInt", Color(0xFF000000), Color(0xFFFFD700)) // 黑金配色
        vipLevelInt >= 1 -> Triple("VIP $vipLevelInt", Color(0xFFD4AF37), Color.White) // 普通 VIP 金色
        else -> Triple("普通用户", Color.LightGray, Color.White) // 非 VIP 灰色
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = user?.headImg ?: "https://picsum.photos/200",
            contentDescription = "Avatar",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .clickable { onAvatarClick() },
            placeholder = painterResource(id = R.drawable.ic_launcher),
            error = painterResource(id = R.drawable.ic_launcher),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(16.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = user?.name ?: "未登录用户",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            // VIP 标签
            VipIcon(
                vipTagText = vipTagText,
                vipBgColor = vipBgColor,
                vipTextColor = vipTextColor,
                onClick = onVipClick
            )
        }
    }
}

@Composable
private fun LoggedOutHeader(onAvatarClick: () -> Unit) {
    val VdGreen = Color(0xFF1AAD19)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onAvatarClick,
            modifier = Modifier
                .weight(0.5f) // 使用 weight 而不是写死宽度比例
                .height(40.dp), // 稍微调整高度使其更紧凑
            colors = ButtonDefaults.buttonColors(
                containerColor = VdGreen,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(50)
        ) {
            Icon(Icons.Filled.Key, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "立即登录",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "登录体验更多精彩内容",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
private fun UserStatsRow(
    user: UserInfo
) {
    val navController = LocalNavController.current // 直接获取控制器

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        // 为关注添加点击事件
        StatItem(user.followCount, "关注") {
            navController.navigateToFollowList()
        }
        StatItem(user.followerCount, "粉丝"){
            navController.navigateToFanList()
        }
        StatItem(user.level, "等级")
        StatItem(user.heardCount, "听歌")
    }
}

@Composable
private fun ActionButtonsRow() {
    // 定义按钮数据
    data class ActionBtn(val icon: ImageVector, val text: String, val color: Color)

    val actions = listOf(
        ActionBtn(Icons.Filled.Star, "提现", Color(0xFFFF9800)),
        ActionBtn(Icons.Filled.Favorite, "会员", Color(0xFF00BFA5)),
        ActionBtn(Icons.Filled.ShoppingCart, "装扮", Color(0xFF2196F3)),
        ActionBtn(Icons.Filled.DateRange, "日签", Color(0xFFF44336))
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        actions.forEach { item ->
            ActionItem(
                icon = item.icon,
                text = item.text,
                iconTint = item.color
            )
        }
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