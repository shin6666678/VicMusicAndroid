package com.shin.vicmusic.feature.myInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.shin.vicmusic.feature.common.icon.VipIcon

@Preview(showBackground = true)
@Composable
fun MyInfoPreview() {
    MyInfoScreen(
        userInfo = UserInfo(
            id = "1",
            name = "上尉诗人",
            headImg = "https://img1.baidu.com/it/u=1302251292,3487620349&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500", // 示例头像
            slogan = "Hello World",
            sex = 1,
            points = 1000,
            mail = "shin@vicmusic.com",
            followCount = 3,
            followerCount = 0,
            level = 6,
            vipLevel = 6,
            experience = 880,
            nextLevelExp = 1000,
            totalListenTime = 7260,
            heardCount = 100,
        ),
        onBackClick = {},
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
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyInfoScreen(
    userInfo: UserInfo?,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            // 透明的 TopAppBar，让背景图能透出来
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White // 白色返回按钮
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: T-shirt icon action */ }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart, // 示例图标
                            contentDescription = "T-shirt",
                            tint = Color.White // 白色图标
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // AppBar 背景透明
                )
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 上半部分背景图
            MyAsyncImage(
                // 使用固定的 URL 或从 UserInfo 获取
                model = "https://images.pexels.com/photos/1323550/pexels-photo-1323550.jpeg",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp), // 固定高度
                contentScale = ContentScale.Crop
            )

            // 可滚动的内容区域
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // 应用 Scaffold 的 padding
                    .verticalScroll(rememberScrollState())
            ) {
                if (userInfo == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 100.dp), // 避免在图像顶部显示加载动画
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // 向上偏移的卡片内容
                    Column(
                        modifier = Modifier
                            .padding(top = 150.dp) // 【修改点】增加了卡片的顶部边距，使其位置更靠下
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp) // 卡片之间的间距
                    ) {
                        // 用户信息主卡片
                        UserInfoCard(user = userInfo)

                        // 等级与经验卡片 (样式已统一)
                        LevelExperienceCard(user = userInfo)

                        // 详细信息列表
                        InfoListSection(user = userInfo)

                        // 底部留白
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun UserInfoCard(user: UserInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        // 半透明效果
        colors = CardDefaults.cardColors(
            // 【修改点】alpha值调高，降低透明度，使卡片更不透明
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // 左侧头像和等级
                Box(modifier = Modifier.size(80.dp)) {
                    MyAsyncImage(
                        model = user.headImg,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .align(Alignment.Center)
                    )
                    // 等级徽章
                    Text(
                        text = "LV${user.level}",
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFDD835), Color(0xFFF57F17))
                                ),
                                shape = RoundedCornerShape(
                                    topStart = 12.dp, bottomEnd = 12.dp, topEnd = 4.dp, bottomStart = 4.dp
                                )
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 中间信息
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (user.sex == 1) "男" else "女",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        VipIcon(vipLevelInt = user.vipLevel)
                    }
                }

                // 右侧编辑按钮
                Button(
                    onClick = { /*TODO*/ },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("编辑资料", fontSize = 12.sp)
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            // 关注、粉丝等信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                FollowerInfoItem("关注", user.followCount)
                FollowerInfoItem("粉丝", user.followerCount)
                // 如果需要，可以从 UserInfo 中获取更多信息
                // FollowerInfoItem("访客", user.visitorCount ?: 0)
            }
        }
    }
}

@Composable
private fun FollowerInfoItem(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 等级和经验条卡片 (样式已与 UserInfoCard 统一)
 */
@Composable
private fun LevelExperienceCard(user: UserInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // 统一圆角
        colors = CardDefaults.cardColors(
            // 【修改点】alpha值调高，降低透明度
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f) // 统一颜色和透明度
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // 统一阴影
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "当前等级",
                    style = MaterialTheme.typography.titleMedium,
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
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${user.experience} / ${user.nextLevelExp} EXP",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )

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
 * 详细信息列表区域 (样式调整)
 */
@Composable
private fun InfoListSection(user: UserInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // 统一圆角
        colors = CardDefaults.cardColors(
            // 【修改点】alpha值调高，降低透明度
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f) // 统一颜色
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            InfoRowItem(icon = Icons.Default.Person, label = "用户ID", value = user.id)
            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.2f))
            InfoRowItem(icon = Icons.Default.Email, label = "邮箱", value = user.mail ?: "未绑定")
            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.2f))
            InfoRowItem(icon = Icons.Default.Info, label = "注册时间", value = "2024-01-01") // 需后端返回
        }
    }
}

@Composable
private fun InfoRowItem(icon: ImageVector, label: String, value: String) {
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
