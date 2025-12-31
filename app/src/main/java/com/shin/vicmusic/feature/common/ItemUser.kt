package com.shin.vicmusic.feature.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.feature.relationship.RelationshipViewModel

@Preview
@Composable
fun ItemUserPreview() {
    ItemUserContent(
        user = UserInfo(
            id = "1",
            name = "张三",
            headImg = "https://picsum.photos/200/300",
            slogan = "Hello World",
            sex = 1,
            points = 100,
            mail = "zhangsan@example.com",
            followCount = 10,
            followerCount = 5,
            level = 1,
            vipLevel = 5,
            heardCount = 20,
            isFollowing = false,
            isFollowingMe = true
        ),
        showFollowStatus = true
    )
}

@Composable
fun ItemUser(
    user: UserInfo,
    showFollowStatus: Boolean = false,
    viewModel: RelationshipViewModel? = null,
) {
    // 检查是否处于预览模式 (Preview Mode)
    if (LocalInspectionMode.current) {
        ItemUserContent(
            user = user,
            onClick = {},
            onFollowClick = {},
            showFollowStatus = showFollowStatus,
        )
        return
    }

    val actualViewModel = viewModel ?: hiltViewModel()
    ItemUserContent(
        user = user,
        onClick = {},
        onFollowClick ={
            actualViewModel.toggleFollow(user.id,0)
        },
        showFollowStatus = showFollowStatus
    )

}

@Composable
fun ItemUserContent(
    user: UserInfo,
    onClick: (String) -> Unit = {},
    onFollowClick: (String) -> Unit = {},
    showFollowStatus: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(user.id) }
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MyAsyncImage(
            model = user.headImg,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = user.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        // 关注按钮
        if (showFollowStatus)
            if (user.isFollowing) {
                OutlinedButton(
                    onClick = { onFollowClick(user.id) },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(Color.LightGray))

                ) {
                    Text(
                        text = "已关注",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            } else {
                OutlinedButton(
                    onClick = { onFollowClick(user.id) },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(Color.Gray))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.Black
                    )
                    Text(
                        text = "关注",
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }
            }
    }
}
