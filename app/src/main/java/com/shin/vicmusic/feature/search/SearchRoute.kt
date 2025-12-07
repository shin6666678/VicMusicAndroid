package com.shin.vicmusic.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.flowlayout.FlowRow
import com.shin.vicmusic.R
import com.shin.vicmusic.core.design.theme.VicMusicTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRoute(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    var searchText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SearchTopBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onBackClick = { navController.popBackStack() },
                onMicClick = { /* Handle mic click */ }
            )
        },
        content = { paddingValues ->
            SearchScreenContent(
                modifier = Modifier.padding(paddingValues),
                onSearchTagClick = { tag -> searchText = tag }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onMicClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // AI助手 button
                Button(
                    onClick = { /* Handle AI assistant click */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F7FA)), // Light blue
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.wrapContentWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_video_comment), // Placeholder icon
                        contentDescription = "AI Assistant",
                        tint = Color(0xFF00BCD4), // Cyan
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "AI助手", color = Color(0xFF00BCD4), fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Search Input Field
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    placeholder = { Text("周杰伦新歌 前天发布", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = onMicClick) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Microphone",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = Color(0xFFF0F0F0),
                        unfocusedContainerColor = Color(0xFFF0F0F0)
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
fun SearchScreenContent(
    modifier: Modifier = Modifier,
    onSearchTagClick: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            CategoryButtons()
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SearchHistorySection(
                historyTags = listOf("Baby dont cry", "关键词", "龙卷风", "莫扎特钢琴曲k448 jaycd"),
                onSearchTagClick = onSearchTagClick,
                onClearHistory = { /* Handle clear history */ }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SearchDiscoverySection(
                discoveryTags = listOf("周杰伦新歌", "baby", "雨的印记 jaycd", "莫扎特钢琴曲k448 jaycd", "exo"),
                onSearchTagClick = onSearchTagClick
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            HotAndTrendingSection()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CategoryButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryButton(iconRes = R.drawable.ic_cloud_country, text = "歌手")
        CategoryButton(iconRes = R.drawable.ic_drawer_toggle, text = "飙升榜")
        CategoryButton(iconRes = R.drawable.ic_app_logo, text = "扑淘商城")
        CategoryButton(iconRes = R.drawable.ic_play_without_circle, text = "助眠专注")
    }
}

@Composable
fun CategoryButton(iconRes: Int, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { /* Handle category click */ }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFF0F0F0), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun SearchHistorySection(
    historyTags: List<String>,
    onSearchTagClick: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "搜索历史", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            IconButton(onClick = onClearHistory) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear History",
                    tint = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            historyTags.forEach { tag ->
                SearchTag(text = tag, onClick = { onSearchTagClick(tag) })
            }
        }
    }
}

@Composable
fun SearchDiscoverySection(
    discoveryTags: List<String>,
    onSearchTagClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "搜索发现", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            discoveryTags.forEach { tag ->
                SearchTag(text = tag, onClick = { onSearchTagClick(tag) })
            }
        }
    }
}

@Composable
fun SearchTag(text: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF0F0F0), // Light gray background
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = Color.Black,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun HotAndTrendingSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Hot Search Section
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "热门搜索", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                repeat(7) { index ->
                    HotSearchItem(
                        rank = index + 1,
                        title = when (index) {
                            0 -> "恒星不忘 forever forever"
                            1 -> "易烊千玺 玺鹭"
                            2 -> "预约年度听歌报告"
                            3 -> "恋人"
                            4 -> "王源跨年演唱会盲盒"
                            5 -> "刘宇CD听雨"
                            else -> "疯狂动物城2"
                        },
                        subtitle = when (index) {
                            0 -> "宇宙级阵容引爆情怀"
                            1 -> "开启「密语」之旅"
                            2 -> "测试你的2025音乐温度"
                            3 -> "李荣浩的《恋人》为何因事件"
                            4 -> "送门票！15款绝美皮肤解锁"
                            5 -> "扑海商品热销中"
                            else -> "79%的人听完"
                        },
                        isRedRank = index < 3 // Ranks 1-3 are red
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // My Trending Section
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "我的热搜", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                repeat(7) { index ->
                    HotSearchItem(
                        rank = index + 1,
                        title = when (index) {
                            0 -> "王心凌 我会好"
                            1 -> "周杰伦 明明就"
                            2 -> "cici_把回忆拼"
                            3 -> "G.E.M. 邓紫棋"
                            4 -> "弦子 舍不得"
                            5 -> "汪苏泷 还给你"
                            else -> "李荣浩 恋人"
                        },
                        subtitle = when (index) {
                            0 -> "你「最近在听」的热搜"
                            1 -> "你怀念的都还在那里"
                            2 -> "请你听她的热搜歌曲"
                            3 -> "从「龙卷风」开始探索"
                            4 -> "请你听她的热搜歌曲"
                            5 -> "你可能喜欢的热门歌曲"
                            else -> "根据你听的「李荣"
                        },
                        isRedRank = index < 3 // Ranks 1-3 are red
                    )
                }
            }
        }
    }
}

@Composable
fun HotSearchItem(
    rank: Int,
    title: String,
    subtitle: String,
    isRedRank: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* Handle item click */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$rank",
            color = if (isRedRank) Color.Red else Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.width(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontSize = 14.sp, color = Color.Black)
            Text(text = subtitle, fontSize = 11.sp, color = Color.Gray)
        }
    }
}




// Previews
@Preview(showBackground = true, widthDp = 360)
@Composable
fun PreviewSearchRoute() {
    VicMusicTheme {
        SearchRoute(rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchTopBar() {
    VicMusicTheme {
        SearchTopBar(
            searchText = "周杰伦新歌",
            onSearchTextChange = {},
            onBackClick = {},
            onMicClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun PreviewSearchScreenContent() {
    VicMusicTheme {
        SearchScreenContent(onSearchTagClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCategoryButtons() {
    VicMusicTheme {
        CategoryButtons()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchHistorySection() {
    VicMusicTheme {
        SearchHistorySection(
            historyTags = listOf("Baby dont cry", "关键词", "龙卷风"),
            onSearchTagClick = {},
            onClearHistory = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchDiscoverySection() {
    VicMusicTheme {
        SearchDiscoverySection(
            discoveryTags = listOf("周杰伦新歌", "baby", "雨的印记 jaycd"),
            onSearchTagClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun PreviewHotAndTrendingSection() {
    VicMusicTheme {
        HotAndTrendingSection()
    }
}
