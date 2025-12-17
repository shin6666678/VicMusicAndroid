package com.shin.vicmusic.feature.me.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // 导入常用的布局组件
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.shin.vicmusic.core.model.User
import com.shin.vicmusic.feature.me.ActionItem // 确保导入了 ActionItem
import com.shin.vicmusic.feature.vip.VipIcon

@Preview
@Composable
fun UserInfoCardPreview() {
    UserInfoCard(
        onAvatarClick = {},
        isLoggedIn = true,
        user = User(
            name = "登录情况测试用户",
            vipLevel = "5",
        )
    )
}

@Composable
fun UserInfoCard(
    onAvatarClick: () -> Unit,
    onVipClick: () -> Unit = {}, // [新增] 接收 VIP 點擊事件
    isLoggedIn: Boolean,
    user: User? // [新增] 接收 User 对象
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // 显式设置背景色，防止透明
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. 头部区域：根据登录状态切换
            if (isLoggedIn) {
                LoggedInHeader(onAvatarClick,onVipClick,user)
            } else {
                LoggedOutHeader(onAvatarClick)
            }

            // 2. 统计数据区域 (仅登录显示)
            if (isLoggedIn) {
                Spacer(modifier = Modifier.height(20.dp))
                UserStatsRow()
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
    user: User?
) {
    // 1. 解析 VIP 等级 (默认为 0)
    val vipLevelInt = user?.vipLevel?.toIntOrNull() ?: 0

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
private fun UserStatsRow() {
    // 定义数据类或直接使用 List<Pair>
    val stats = listOf(
        "null" to "关注",
        "null" to "粉丝",
        "null" to "等级",
        "null" to "听歌"
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        stats.forEach { (count, label) ->
            StatItem(count, label)
        }
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
fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
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