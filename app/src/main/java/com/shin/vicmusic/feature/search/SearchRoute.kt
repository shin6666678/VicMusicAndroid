package com.shin.vicmusic.feature.search

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.flowlayout.FlowRow
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.feature.search.component.HotAndTrendingSection
import com.shin.vicmusic.feature.search.component.SearchTopBar
import com.shin.vicmusic.feature.search.result.SearchResultContent

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
                // 只有在已经搜索过后才显示 Tabs
                if (uiState.hasSearched) {
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
                } else if (!uiState.hasSearched) {
                    // Default Content (History, Hot Search) - 当没有搜索过时显示
                    SearchDefaultContent(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        historyTags = uiState.searchHistory,
                        onSearchTagClick = { tag ->
                            viewModel.onEvent(SearchEvent.QueryChanged(tag))
                            viewModel.onEvent(SearchEvent.Search)
                        },
                        onClearHistory = { viewModel.onEvent(SearchEvent.ClearHistory) }
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
fun SearchDefaultContent(
    modifier: Modifier = Modifier,
    historyTags: List<String>,
    onSearchTagClick: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (historyTags.isNotEmpty()) {
            item {
                SearchHistorySection(
                    historyTags = historyTags,
                    onSearchTagClick = onSearchTagClick,
                    onClearHistory = onClearHistory
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
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
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF5F5F5),
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 14.sp
        )
    }
}