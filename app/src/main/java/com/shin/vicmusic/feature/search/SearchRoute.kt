package com.shin.vicmusic.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.flowlayout.FlowRow
import com.shin.vicmusic.R
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.*
import com.shin.vicmusic.feature.common.ItemArtist
import com.shin.vicmusic.feature.common.ItemPlaylist
import com.shin.vicmusic.feature.common.ItemSong
import com.shin.vicmusic.feature.common.ItemUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRoute(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SearchTopBar(
                searchText = uiState.query,
                onSearchTextChange = { viewModel.onEvent(SearchEvent.QueryChanged(it)) },
                onBackClick = { navController.popBackStack() },
                onMicClick = { /* Handle mic click */ },
                onSearch = { viewModel.onEvent(SearchEvent.Search) },
                onClear = { viewModel.onEvent(SearchEvent.ClearQuery) }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Search Tabs
                if (uiState.query.isNotBlank() || uiState.hasSearched) {
                    ScrollableTabRow(
                        selectedTabIndex = uiState.selectedTab.ordinal,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                        edgePadding = 16.dp,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab.ordinal]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        divider = {}
                    ) {
                        SearchTab.values().forEach { tab ->
                            Tab(
                                selected = uiState.selectedTab == tab,
                                onClick = { viewModel.onEvent(SearchEvent.TabChanged(tab)) },
                                text = { Text(text = tab.title) }
                            )
                        }
                    }
                }

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.error != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.error ?: "Error", color = Color.Red)
                    }
                } else if (!uiState.hasSearched && uiState.query.isBlank()) {
                    // Default Content (History, Hot Search)
                    SearchDefaultContent(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onSearchTagClick = { tag ->
                            viewModel.onEvent(SearchEvent.QueryChanged(tag))
                            viewModel.onEvent(SearchEvent.Search)
                        }
                    )
                } else {
                    // Search Results
                    SearchResultContent(
                        uiState = uiState,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    )
}

@Composable
fun SearchResultContent(
    uiState: SearchUiState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        if (uiState.selectedTab == SearchTab.COMPREHENSIVE) {
            uiState.comprehensiveResult?.let { result ->
                if (result.songs.isNotEmpty()) {
                    item { SectionTitle("单曲") }
                    items(result.songs) { song ->
                        ItemSong(song = song)
                    }
                }
                if (result.playlists.isNotEmpty()) {
                    item { SectionTitle("歌单") }
                    items(result.playlists) { playlist ->
                        // 修正参数名，假设为 playList (参考之前上传的 PlayList.kt 风格) 或 playlist
                        ItemPlaylist(playlist = playlist, onClick = {})
                    }
                }
                if (result.albums.isNotEmpty()) {
                    item { SectionTitle("专辑") }
                    items(result.albums) { album ->
                        // Album 复用 ItemPlaylist 样式
                        ItemPlaylist(
                            playlist = Playlist(album.id, "", album.title, album.icon, "", 0),
                            onClick = {}
                        )
                    }
                }
                if (result.artists.isNotEmpty()) {
                    item { SectionTitle("歌手") }
                    items(result.artists) { artist ->
                        ItemArtist(artist = artist, onClick = {})
                    }
                }
                if (result.users.isNotEmpty()) {
                    item { SectionTitle("用户") }
                    items(result.users) { user ->
                        ItemUser(user = user, onClick = {})
                    }
                }

                if (result.songs.isEmpty() && result.playlists.isEmpty() && result.albums.isEmpty() && result.artists.isEmpty() && result.users.isEmpty()) {
                    item { EmptyState() }
                }
            }
        } else {
            // Specific Lists
            if (uiState.listResult.isEmpty()) {
                item { EmptyState() }
            } else {
                items(uiState.listResult) { item ->
                    when (item) {
                        is Song -> ItemSong(song = item)
                        is Playlist -> ItemPlaylist(playlist = item, onClick = {})
                        is Album -> ItemPlaylist(playlist = Playlist(item.id, "", item.title, item.icon, "", 0), onClick = {})
                        is Artist -> ItemArtist(artist = item, onClick = {})
                        is UserInfo -> ItemUser(user = item, onClick = {})
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
        Text("未找到相关内容", color = Color.Gray)
    }
}

// ... SectionTitle, SearchTopBar, SearchDefaultContent, etc. (保持不变) ...
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onMicClick: () -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
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

                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    placeholder = { Text("搜索歌曲、歌单、歌手...", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = onClear) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                            }
                        } else {
                            IconButton(onClick = onMicClick) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Microphone",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(25.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = Color(0xFFF0F0F0),
                        unfocusedContainerColor = Color(0xFFF0F0F0)
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch() })
                )

                TextButton(onClick = onSearch) {
                    Text("搜索", color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
fun SearchDefaultContent(
    modifier: Modifier = Modifier,
    onSearchTagClick: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            CategoryButtons()
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SearchHistorySection(
                historyTags = listOf("Baby dont cry", "关键词", "龙卷风"),
                onSearchTagClick = onSearchTagClick,
                onClearHistory = { /* Handle clear history */ }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SearchDiscoverySection(
                discoveryTags = listOf("周杰伦新歌", "baby", "雨的印记 jaycd"),
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

// 辅助组件 (CategoryButtons, SearchHistorySection等) 保持原样...
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
                repeat(3) { index ->
                    HotSearchItem(
                        rank = index + 1,
                        title = "热门歌曲 ${index + 1}",
                        subtitle = "副标题 ${index + 1}",
                        isRedRank = index < 3
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