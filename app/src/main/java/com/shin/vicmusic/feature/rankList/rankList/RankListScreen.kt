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


@Composable
fun RankListRoute(
    navController: NavController = LocalNavController.current,
    viewModel: RankListViewModel = hiltViewModel()
) {

    val rankListPeaks by viewModel.rankListPeaks.collectAsState()

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
    peakCharts: List<RankListPeak> = emptyList(),
    popBackStack: () -> Unit = {},
    onRankClick: (String) -> Unit = {}
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
            // 巅峰榜垂直列表
            items(peakCharts) { item ->
                RankListItem(
                    rankListItemInfo = item,
                    modifier = Modifier.clickable { onRankClick(item.id) }
                )
            }
        }
    }
}
