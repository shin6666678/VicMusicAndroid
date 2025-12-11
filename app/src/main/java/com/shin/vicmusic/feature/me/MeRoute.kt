package com.shin.vicmusic.feature.me

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shin.vicmusic.core.model.User
import com.shin.vicmusic.feature.auth.AuthViewModel
import com.shin.vicmusic.feature.liked.LikedScreen
import com.shin.vicmusic.feature.me.component.MeTopBar
import com.shin.vicmusic.feature.me.component.TopNotifyBar
import com.shin.vicmusic.feature.me.component.UserInfoCard
import com.shin.vicmusic.util.getAuthViewModelSingleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeRoute(
    onAvatarClick: () -> Unit = {},
    authViewModel: AuthViewModel = getAuthViewModelSingleton()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState() // [新增] 观察 currentUser

    // [新增] 如果已登录但无用户信息，尝试获取
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == true && currentUser == null) {
            authViewModel.fetchUserInfo()
        }
    }

    // 添加 onAvatarClick 参数
    Log.d("MeRoute", "isLoggedIn: $isLoggedIn")
    MeScreen(
        onAvatarClick = onAvatarClick,
        isLoggedIn = isLoggedIn ?: false ,// [修复] 确保状态为非空Boolean，null时默认为false
        user = currentUser // [新增] 传递 user
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen(
    onAvatarClick: () -> Unit = {},
    isLoggedIn: Boolean,
    user: User? = null // [新增] 接收参数
) {
    // [新增] 控制是否显示“喜欢列表”的状态
    var showLikedList by remember { mutableStateOf(false) }

    if (showLikedList) {
        // [新增] 显示喜欢列表，传入返回回调
        LikedScreen(
            onBack = { showLikedList = false }
        )
    }else{
        Scaffold(
            topBar = {
                MeTopBar()
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                //通知栏
                Spacer(Modifier.height(8.dp))
                TopNotifyBar()

                // User Info Card
                Spacer(Modifier.height(16.dp))
                UserInfoCard(
                    onAvatarClick = onAvatarClick,
                    isLoggedIn = isLoggedIn,
                    user = user // [修改] 传入 user
                )// 传入 onAvatarClick

                // Quick Access Icons
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // [修改] 传入 onClick 回调
                    QuickAccessItem(
                        icon = Icons.Filled.Favorite,
                        text = "收藏",
                        count = "2", // 这里的数据后续可以绑定 ViewModel
                        onClick = { showLikedList = true }
                    )
                    QuickAccessItem(icon = Icons.Filled.Download, text = "本地", count = "29")
                    QuickAccessItem(icon = Icons.Filled.Headphones, text = "有声", count = "6")
                    QuickAccessItem(icon = Icons.Filled.ReceiptLong, text = "已购", count = "1")
                }

                // Recently Played Section
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "最近播放",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "更多",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowRight,
                            contentDescription = "More",
                            tint = Color.Gray
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf("1", "2", "3", "4", "5")) { index ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(
                                model = "https://picsum.photos/100/100?random=$index",
                                contentDescription = "Album Cover",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("已播放歌曲", style = MaterialTheme.typography.bodySmall)
                            Text(
                                "2245首",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Song Lists Section
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { /* Handle tab click */ }
                    ) {
                        Text(
                            text = "自建歌单",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(Modifier.height(4.dp))
                        Divider(
                            modifier = Modifier
                                .width(60.dp)
                                .height(2.dp)
                                .background(Color(0xFF00BFA5))
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { /* Handle tab click */ }
                    ) {
                        Text(
                            text = "收藏歌单",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(4.dp))
                        Divider(
                            modifier = Modifier
                                .width(60.dp)
                                .height(2.dp)
                                .background(Color.Transparent)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Playlist",
                        tint = Color.Gray
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "More Playlists",
                        tint = Color.Gray
                    )
                }
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2F5))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = "https://picsum.photos/50/50?random=6",
                            contentDescription = "Playlist Cover",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("sb", style = MaterialTheme.typography.bodyMedium)
                            Text("0首", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp)) // Add some space at the bottom of the scrollable content
            }
        }
    }

}

@Composable
fun QuickAccessItem(
    icon: ImageVector,
    text: String,
    count: String,
    onClick: () -> Unit = {} // [新增] 默认为空
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(4.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall)
        Text(text = count, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
fun ActionItem(
    icon: ImageVector,
    text: String,
    iconTint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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


@Preview
@Composable
fun MeScreenPreview() {
    MeScreen(
        isLoggedIn = false
    )
}
