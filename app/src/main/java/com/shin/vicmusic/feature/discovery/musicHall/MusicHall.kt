package com.shin.vicmusic.feature.discovery.musicHall

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONGS
import com.shin.vicmusic.feature.discovery.DiscoveryViewModel
import com.shin.vicmusic.feature.song.ItemSong

@Preview
@Composable
fun MusicHallPreview() {
    MusicHall(songs = SONGS)
}
@Composable
fun MusicHall(
    viewModel: DiscoveryViewModel = hiltViewModel(),
    songs:List<Song>,
    // [新增] 传递回调
    onLikeClick: (Song) -> Unit = {},
    onQuickAccessClick: (String) -> Unit = {}
) {
    val playerManager = LocalPlayerManager.current
    val listState = rememberLazyListState()

    // 监听列表滚动到底部，触发加载更多
    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) {
                false
            } else {
                val lastVisibleItem = visibleItemsInfo.last()
                // 如果最后一个可见项的索引等于总项数-1（或者是倒数第几个），则认为到底部了
                // 这里的预加载阈值设为 2，表示倒数第2项出现时就开始加载
                lastVisibleItem.index >= totalItems - 2
            }
        }
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom) {
            viewModel.loadData()
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp) // 底部留白给播放条
    ) {
        // 1. 顶部 Banner 区域
        item {
            BannerSection()
        }

        // 2. 金刚区 (功能入口：每日推荐、歌单等)
        item {
            QuickAccessSection(onItemClick = onQuickAccessClick)
        }

        // 3. 可以在这里加 "推荐歌单" 的横向滚动列表 (这里先留空)

        // 4. "全部播放" 悬浮条头
        item {
            PlayAllHeader(count = songs.size)
        }

        // 5. 歌曲列表 (复用你刚才改好的 ItemSong)
        items(songs) { song ->
            ItemSong(
                song = song,
                modifier = Modifier.clickable { playerManager.playSong(song)} ,
                onAddToQueueClick = { playerManager.addSongToQueue(song) },
                onLikeClick = onLikeClick
            )
        }

        // 6. 底部加载指示器 (可选)
        item {
           Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
               // 这里可以根据 loading 状态显示/隐藏
               // 由于 loading 状态在 VM 内部控制，这里暂时简单放置占位
               // 若要精确控制，需从 VM 暴露 isLoading State
           }
        }
    }
}

// --- 子组件实现 ---

@Composable
fun BannerSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray) // 占位背景
    ) {
        // 模拟一个紫色渐变 Banner
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF6200EE), Color(0xFFBB86FC))
                    )
                )
        ) {
            Text(
                text = "新歌首发 · 听见未来",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }
    }
}

data class QuickAccessMenu(
    val label: String,
    val icon: ImageVector
)
@Composable
fun QuickAccessSection(onItemClick: (String) -> Unit = {}) {
    // 定义菜单数据列表
    val menus = listOf(
        //QuickAccessMenu("每日推荐", Icons.Default.CalendarToday),
        QuickAccessMenu("歌手", Icons.Default.Mic), // 使用 Mic 或 Person
        QuickAccessMenu("排行", Icons.Default.BarChart), // 使用柱状图代表排行
        QuickAccessMenu("歌单", Icons.AutoMirrored.Filled.QueueMusic), // 使用 QueueMusic
        QuickAccessMenu("专辑", Icons.Default.Album),
        QuickAccessMenu("电台", Icons.Default.Radio)
    )
    // 定义嵌套滑动连接，拦截并消耗剩余的滑动事件
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            // 处理拖动结束后的剩余滑动 (Overscroll)
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // 将所有剩余的 available 滑动距离全部消耗掉，返回 available 表示"我处理了"
                // 这样父容器（如 HorizontalPager）收到的剩余滑动就是 0，就不会触发翻页了
                return available
            }

            // 处理惯性滑动结束后的剩余速度
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                // 同样消耗掉所有剩余速度
                return available
            }
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp) // 增加一点垂直间距
            .nestedScroll(nestedScrollConnection), // [关键] 应用拦截器
        contentPadding = PaddingValues(horizontal = 24.dp), // [关键] 列表左右的留白，滑动时可以滑出屏幕边缘
        horizontalArrangement = Arrangement.spacedBy(28.dp) // [关键] 图标之间的间距
    ) {
        items(menus) { menu ->
            QuickAccessItem(
                icon = menu.icon,
                label = menu.label,
                onClick = {
                    onItemClick(menu.label)
                }
            )
        }
    }
}

@Composable
fun QuickAccessItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {} // [新增] 点击回调
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            // [关键] 1. 先裁剪形状，保证水波纹也是圆角的
            .clip(RoundedCornerShape(8.dp))
            // [关键] 2. 添加点击事件，必须在 padding 之前，否则点击区域会很小
            .clickable(onClick = onClick)
            // [关键] 3. 添加 padding，增加点击热区，同时防止内容贴边
            .padding(4.dp)
    ) {
        // 圆形图标背景
        Surface(
            shape = CircleShape,
            // 使用 primaryContainer 配合透明度，做成淡色背景
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary, // 图标颜色
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1 // 限制一行
        )
    }
}

@Composable
fun PlayAllHeader(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "播放全部",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "播放全部",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "($count)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant // 灰色数字
        )
        Spacer(modifier = Modifier.weight(1f))

    }
}