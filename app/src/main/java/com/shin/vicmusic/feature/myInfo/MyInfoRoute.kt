package com.shin.vicmusic.feature.myInfo

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Feed
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.feature.common.MyAsyncImage
import com.shin.vicmusic.feature.common.icon.VipIcon
import com.shin.vicmusic.feature.feed.component.FeedItemCard
import com.shin.vicmusic.feature.myInfo.edit.navigateToMyInfoEdit
import com.shin.vicmusic.util.copyUriToCache

@Preview(showBackground = true)
@Composable
fun MyInfoPreview() {
    MyInfoScreen(
        userInfo = UserInfo(
            id = "1",
            name = "上尉诗人",
            headImg = "",
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
        uiState = MyInfoUiState(),
        userFeeds = emptyList(),
        isFeedLoading = false,
        isMe = true,
        snackbarHostState = remember { SnackbarHostState() },
        onEditClick = {},
        onFollowClick = {},
        onBgClick = {}
    )
}

@Composable
fun MyInfoRoute(
    viewModel: MyInfoViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // 处理消息提示
    LaunchedEffect(uiState.error, uiState.message) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // 图片选择器
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val localPath = copyUriToCache(context, it)
            if (localPath != null) {
                viewModel.updateUserBg(localPath)
            }
        }
    }

    // 如果未登录，直接退回
    if (isLoggedIn == false) {
        navController.popBackStack()
        return
    }

    val isMe = isLoggedIn == true && (currentUser?.id == uiState.userInfo?.id || uiState.userInfo == null)
    val displayUserInfo = if (isMe) currentUser else uiState.userInfo

    MyInfoScreen(
        userInfo = displayUserInfo,
        userFeeds = uiState.userFeeds,
        isFeedLoading = uiState.isFeedLoading,
        isMe = isMe,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = { navController.popBackStack() },
        onEditClick = navController::navigateToMyInfoEdit,
        onFollowClick = { displayUserInfo?.id?.let { viewModel.toggleFollow(it) } },
        onBgClick = {
            if (isMe) {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyInfoScreen(
    userInfo: UserInfo?,
    userFeeds: List<Feed>,
    isFeedLoading: Boolean,
    isMe: Boolean,
    uiState: MyInfoUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClick: () -> Unit,
    onEditClick: () -> Unit = {},
    onFollowClick: () -> Unit = {},
    onBgClick: () -> Unit = {}
) {
    val cardHeightDp = remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "T-shirt",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                )
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 可滚动的内容区域
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 32.dp)
            ) {
                if (userInfo == null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    //上部分
                    item{
                        // 在 item 内部
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clickable(enabled = !uiState.isLoading, onClick = onBgClick)
                        ) {
                            // 背景图片
                            MyAsyncImage(
                                model = userInfo.bgImg,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // 底部居中的用户信息卡片，中心线与底部对齐
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .onGloballyPositioned { coords ->
                                        val heightPx = coords.size.height
                                        val density = density
                                        with(density) {
                                            cardHeightDp.value = heightPx.toDp()
                                        }
                                    }
                                    .offset(y = cardHeightDp.value / 2)
                            ) {
                                UserInfoCard(
                                    user = userInfo,
                                    isMe = isMe,
                                    onEditClick = onEditClick,
                                    onFollowClick = onFollowClick
                                )
                            }

                            // 加载遮罩
                            if (uiState.isLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color.White)
                                }
                            }
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(top = cardHeightDp.value / 2 + 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            LevelExperienceCard(user = userInfo)

                            InfoListSection(user = userInfo)
                            
                            Text(
                                text = "动态列表",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                    
                    if (isFeedLoading && userFeeds.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    } else if (userFeeds.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(text = "暂无动态", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        items(userFeeds, key = { it.id }) { feed ->
                            FeedItemCard(
                                feed = feed,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserInfoCard(
    user: UserInfo,
    isMe: Boolean,
    onEditClick: () -> Unit = {},
    onFollowClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(modifier = Modifier.size(80.dp)) {
                    MyAsyncImage(
                        model = user.headImg,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .align(Alignment.Center)
                    )
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

                FilledTonalButton(
                    onClick = if (isMe) onEditClick else onFollowClick,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isMe || !(user.isFollowing ?: false))
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                        contentColor = if (isMe || !(user.isFollowing ?: false))
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text(
                        text = if (isMe) "编辑资料" else if (user.isFollowing == true) "已关注" else "关注",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                FollowerInfoItem("关注", user.followCount)
                FollowerInfoItem("粉丝", user.followerCount)
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

@Composable
private fun InfoListSection(user: UserInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            InfoRowItem(icon = Icons.Default.Person, label = "用户ID", value = user.id)
            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.2f))
            InfoRowItem(icon = Icons.Default.Email, label = "邮箱", value = user.mail ?: "未绑定")
            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.2f))
            InfoRowItem(icon = Icons.Default.Info, label = "注册时间", value = "2024-01-01")
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