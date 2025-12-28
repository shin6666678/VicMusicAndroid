package com.shin.vicmusic.feature.myInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.feature.common.MyAsyncImage
import com.shin.vicmusic.feature.vip.VipIcon

@Preview
@Composable
fun MyInfoPreview() {
    MyInfoScreen(
        userInfo = UserInfo(
            id = "1",
            name = "Shin",
            headImg = "https://picsum.photos/200",
            slogan = "Hello World",
            sex = 1,
            points = 1000,
            mail = "shin@vicmusic.com",
            followCount = 10,
            followerCount = 20,
            level = 5,
            vipLevel = 1,
            heardCount = 100
        ),
        onBackClick = {},
        onLogoutClick = {}
    )
}
@Composable
fun MyInfoRoute(
    viewModel: MyInfoViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    // 如果未登录，直接退回
    if (isLoggedIn == false) {
        navController.popBackStack()
        return
    }

    MyInfoScreen(
        userInfo = currentUser,
        onBackClick = { navController.popBackStack() },
        onLogoutClick = {
            viewModel.logout()
            navController.popBackStack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyInfoScreen(
    userInfo: UserInfo?,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "个人资料") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (userInfo == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // 允许滚动
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. 顶部大头像与基本信息
                ProfileHeader(userInfo)

                Spacer(modifier = Modifier.height(24.dp))

                // 2. 等级与经验卡片 (核心需求)
                LevelExperienceCard(userInfo)

                Spacer(modifier = Modifier.height(16.dp))

                // 3. 详细信息列表
                InfoListSection(userInfo)

                Spacer(modifier = Modifier.weight(1f)) // 把按钮顶到底部

                Spacer(modifier = Modifier.height(32.dp))

                // 4. 退出登录按钮
                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("退出登录", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * 顶部头像区域
 */
@Composable
private fun ProfileHeader(user: UserInfo) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        MyAsyncImage(
            model = user.headImg,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            if (user.vipLevel > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                VipIcon(vipLevelInt = user.vipLevel)
            }
        }
        if (user.slogan.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user.slogan,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

/**
 * 等级和经验条卡片
 */
@Composable
private fun LevelExperienceCard(user: UserInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 等级标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "当前等级",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Lv.${user.level}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 经验条
            val progress = if (user.nextLevelExp > 0) {
                user.experience.toFloat() / user.nextLevelExp.toFloat()
            } else 0f

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant // 更深的背景色
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 经验数值和听歌时长
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${user.experience} / ${user.nextLevelExp} EXP",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )

                // 格式化时长
                val hours = user.totalListenTime / 3600
                val minutes = (user.totalListenTime % 3600) / 60
                Text(
                    text = "总听歌: ${hours}小时${minutes}分",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

/**
 * 详细信息列表区域
 */
@Composable
private fun InfoListSection(user: UserInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // 白色或深色背景
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            InfoRowItem(
                icon = Icons.Default.Person,
                label = "用户ID",
                value = user.id
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.2f))
            InfoRowItem(
                icon = Icons.Default.Email,
                label = "邮箱",
                value = user.mail ?: "未绑定"
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.2f))
            InfoRowItem(
                icon = Icons.Default.Info,
                label = "注册时间",
                value = "2024-01-01" // 这里需要后端返回注册时间，暂时写死或处理日期格式
            )
        }
    }
}

@Composable
private fun InfoRowItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 1
        )
    }
}