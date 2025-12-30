package com.shin.vicmusic.feature.rankList.rankList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.RankListPeak
import com.shin.vicmusic.feature.common.bar.CommonTopBar
import com.shin.vicmusic.feature.rankList.rankList.component.RankListItem
import com.shin.vicmusic.feature.rankList.rankList.component.RecommendedRankListInfo
import com.shin.vicmusic.feature.rankList.rankList.component.RecommendedRankListItem
import com.shin.vicmusic.feature.rankList.rankListDetail.navigateToRankListDetail


@Preview
@Composable
fun RankListScreenPreview() {
}

@Composable
fun RankListRoute(
    navController: NavController = LocalNavController.current,
    viewModel: RankListViewModel = hiltViewModel()
) {

    val rankListPeaks by viewModel.rankListPeaks.collectAsState()

    // 判空处理 (Null/Empty Check)
    if (rankListPeaks.isNullOrEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(), // fillMaxSize(填满最大尺寸)
            contentAlignment = Alignment.Center // Center(居中)
        ) {
            CircularProgressIndicator() // CircularProgressIndicator(圆形进度条)
        }
    } else {
        RankListScreen(
            peakCharts = rankListPeaks!!,
            popBackStack = navController::popBackStack,
            onRankClick = navController::navigateToRankListDetail
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankListScreen(
    recommendedRankLists: List<RecommendedRankListInfo> = emptyList(),
    peakCharts: List<RankListPeak> = emptyList(),
    popBackStack: () -> Unit = {},
    onRankClick: (String) -> Unit = {} // 新增点击回调
) {
    Scaffold(
        topBar = { CommonTopBar(
            popBackStack=popBackStack,
            midText = "维克音乐排行榜"
        ) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            item {
                // 推荐 (Recommended) Section
                Text(
                    text = "推荐",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recommendedRankLists) { item ->
                        RecommendedRankListItem(rankListInfo = item)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                // 巅峰榜 (Peak Charts) Section
                Text(
                    text = "巅峰榜",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            // 4. 巅峰榜垂直列表 (修复：直接使用 items，不要嵌套 LazyColumn)
            items(peakCharts) { item ->
                RankListItem(
                    rankListItemInfo = item,
                    modifier = Modifier.clickable { onRankClick(item.id) }
                )
            }
        }
    }
}
