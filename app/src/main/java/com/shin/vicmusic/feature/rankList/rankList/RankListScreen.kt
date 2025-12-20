package com.shin.vicmusic.feature.rankList.rankList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.feature.common.CommonTopBar
import com.shin.vicmusic.feature.rankList.rankList.RankListViewModel
import com.shin.vicmusic.feature.rankList.rankList.component.RankListItem
import com.shin.vicmusic.feature.rankList.rankList.component.RankListItemInfo
import com.shin.vicmusic.feature.rankList.rankList.component.RecommendedRankListInfo
import com.shin.vicmusic.feature.rankList.rankList.component.RecommendedRankListItem
import com.shin.vicmusic.feature.rankList.rankList.component.Song

@Preview
@Composable
fun RankListScreenPreview() {
    val recommendedRankLists = listOf(
        RecommendedRankListInfo("腾讯音乐榜", Color(0xFF42A5F5)), // Blue
        RecommendedRankListInfo("巅峰潮流榜", Color(0xFFEF5350)), // Red
        RecommendedRankListInfo("韩国Melon榜", Color(0xFF66BB6A)), // Green
        RecommendedRankListInfo("日本Oricon榜", Color(0xFFFFA726)) // Orange
    )

    val peakCharts = listOf(
        RankListItemInfo(
            imageUrl = "https://via.placeholder.com/150/0000FF/FFFFFF?text=Chart1",
            title = "巅峰潮流榜_QQ音乐 x 微博",
            songs = listOf(
                Song("不渝", "梓渝"),
                Song("深海漫游指南", "梓渝"),
                Song("全世界下雨", "周深")
            )
        ),
        RankListItemInfo(
            imageUrl = "https://via.placeholder.com/150/FF0000/FFFFFF?text=Chart2",
            title = "飙升榜_21首新歌上榜",
            songs = listOf(
                Song("恒星不忘 Forever Forever", "周杰"),
                Song("奇迹航线", "马嘉祺"),
                Song("爱与欠", "黄子弘凡")
            )
        ),
        RankListItemInfo(
            imageUrl = "https://via.placeholder.com/150/00FF00/FFFFFF?text=Chart3",
            title = "热歌榜_23首新歌上榜",
            songs = listOf(
                Song("奇迹航线", "马嘉祺"),
                Song("爱错", "王力宏"),
                Song("恋人", "李荣浩")
            )
        ),
        RankListItemInfo(
            imageUrl = "https://via.placeholder.com/150/00FFFF/FFFFFF?text=Chart4",
            title = "新歌榜_14:00 更新",
            songs = listOf(
                Song("街角的晚风", "陈小春"),
                Song("恒星不忘 Forever Forever", "周杰"),
                Song("爱与欠", "黄子弘凡")
            )
        )
    )

    RankListScreen(
        recommendedRankLists = recommendedRankLists,
        peakCharts = peakCharts
    )
}

@Composable
fun RankListRoute(
    navController: NavController = LocalNavController.current,
    viewModel: RankListViewModel = hiltViewModel()
) {

    val rankData by viewModel.rankData.collectAsState()

    // 数据映射：将 DTO 转换为 UI 组件所需的模型
    val recommendedRankLists = rankData?.recommends?.map {
        RecommendedRankListInfo(it.title, Color(it.color))
    } ?: emptyList()


    val peakCharts = rankData?.peaks?.map { peak ->
        RankListItemInfo(
            imageUrl = peak.img,
            title = peak.title,
            songs = peak.top3.map { Song(it.name, it.artist) }
        )
    } ?: emptyList()

    RankListScreen(
        recommendedRankLists = recommendedRankLists,
        peakCharts = peakCharts,
        popBackStack = navController::popBackStack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankListScreen(
    recommendedRankLists: List<RecommendedRankListInfo> = emptyList(),
    peakCharts: List<RankListItemInfo> = emptyList(),
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
                    modifier = Modifier.clickable { onRankClick(item.title) } // 绑定点击事件
                )
            }
        }
    }
}
