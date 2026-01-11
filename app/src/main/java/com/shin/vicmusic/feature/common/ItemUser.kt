package com.shin.vicmusic.feature.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.feature.relationship.RelationshipViewModel

@Preview(showBackground = true)
@Composable
fun ItemUserPreview() {
    Column {
        // 预览：未关注状态
        ItemUserContent(
            user = UserInfo(
                id = "1", name = "张三的长名字测试长名字长名字", headImg = "", slogan = "这是一句很长的个性签名，测试是否会省略显示",
                sex = 1, points = 100, mail = "", followCount = 10, followerCount = 5, level = 1,
                vipLevel = 5, heardCount = 20, isFollowing = false, isFollowingMe = true
            ),
            showFollowStatus = true,
            showSlogan = true
        )
        Divider()
        // 预览：已关注状态 + 私信按钮
        ItemUserContent(
            user = UserInfo(
                id = "2", name = "李四", headImg = "", slogan = "Hello",
                sex = 0, points = 0, mail = "", followCount = 0, followerCount = 0, level = 0,
                vipLevel = 0, heardCount = 0, isFollowing = true, isFollowingMe = false
            ),
            showFollowStatus = true,
            showPMButton = true,
            showSlogan = true
        )
    }
}

@Composable
fun ItemUser(
    user: UserInfo,
    modifier: Modifier = Modifier, // 允许外部传入 Modifier
    showFollowStatus: Boolean = false,
    showPMButton: Boolean = false,
    showSlogan: Boolean = false,
    viewModel: RelationshipViewModel? = null,
    onMessageClick: (() -> Unit) = {},
    onItemClick: ((String) -> Unit)? = null // 显式把点击事件暴露出来
) {
    // 预览模式处理
    if (LocalInspectionMode.current) {
        ItemUserContent(
            user = user,
            showFollowStatus = showFollowStatus,
            showPMButton = showPMButton,
            showSlogan = showSlogan
        )
        return
    }

    val actualViewModel = viewModel ?: hiltViewModel()
    ItemUserContent(
        user = user,
        modifier = modifier,
        showSlogan = showSlogan,
        showPMButton = showPMButton,
        showFollowStatus = showFollowStatus,
        onClick = { onItemClick?.invoke(user.id) },
        onFollowClick = {
            actualViewModel.toggleFollow(user.id, 0)
        },
        onMessageClick = onMessageClick
    )
}

@Composable
fun ItemUserContent(
    user: UserInfo,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
    showSlogan: Boolean = false,
    onFollowClick: (String) -> Unit = {},
    showPMButton: Boolean = false,
    showFollowStatus: Boolean = false,
    onMessageClick: (() -> Unit) = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(user.id) }
            .background(MaterialTheme.colorScheme.surface) // 使用主题背景色
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像
        MyAsyncImage(
            model = user.headImg,
            modifier = Modifier
                .size(50.dp) //稍微调小一点，56dp作为列表项头像略大，可根据设计稿调整
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 中间文本区域
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1, // 限制单行
                overflow = TextOverflow.Ellipsis // 超出显示省略号
            )
            if (showSlogan && user.slogan.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = user.slogan,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // 次要文字颜色
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 右侧按钮区域
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp) // 按钮之间的间距
        ) {
            // 私信按钮
            if (showPMButton) {
                UserActionButton(
                    text = "私信",
                    onClick = onMessageClick,
                    isHighlight = false
                )
            }

            // 关注按钮
            if (showFollowStatus) {
                if (user.isFollowing) {
                    UserActionButton(
                        text = "已关注",
                        onClick = { onFollowClick(user.id) },
                        isHighlight = false,
                    )
                } else {
                    UserActionButton(
                        text = "关注",
                        onClick = { onFollowClick(user.id) },
                        isHighlight = true,
                    )
                }
            }
        }
    }
}


@Composable
private fun UserActionButton(
    text: String,
    onClick: () -> Unit,
    isHighlight: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    val containerColor = if (isHighlight) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (isHighlight) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline
    val borderColor = if (isHighlight) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)

    // 使用 Surface 或 OutlinedButton 自定义样式
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(28.dp),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = if (isHighlight) null else ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(borderColor)
        )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(2.dp))
        }
        Text(
            text = text,
            fontSize = 12.sp,
            color = contentColor
        )
    }
}