package com.shin.vicmusic.feature.me.followList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.feature.common.ItemArtist
import com.shin.vicmusic.feature.artist.artistDetail.navigateToArtistDetail
import com.shin.vicmusic.feature.common.bar.CommonTopBar
import com.shin.vicmusic.feature.common.MyAsyncImage

@Composable
fun FollowListScreen(
    viewModel: FollowListViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current

    LaunchedEffect(Unit) { viewModel.loadData() }

    Scaffold(
        topBar = {
            CommonTopBar(
                midText = "我的关注",
                popBackStack = { navController.popBackStack() })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = viewModel.tabIndex) {
                Tab(
                    selected = viewModel.tabIndex == 0,
                    onClick = { viewModel.switchTab(0) },
                    text = { Text("用户") })
                Tab(
                    selected = viewModel.tabIndex == 1,
                    onClick = { viewModel.switchTab(1) },
                    text = { Text("歌手") })
            }

            if (viewModel.isLoading) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if (viewModel.tabIndex == 0) {
                        if (viewModel.userList.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("没有关注用户")
                                }
                            }
                        } else items(viewModel.userList) { user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { /* 预留跳转(TODO Jump) */ }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MyAsyncImage(
                                    model = user.headImg,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(Modifier.width(16.dp))
                                Text(user.name ?: "", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    } else {
                        if (viewModel.artistList.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("没有关注歌手")
                                }
                            }
                        } else items(viewModel.artistList) { artist ->
                            ItemArtist(
                                artist = artist,
                                onClick = navController::navigateToArtistDetail
                            )
                        }
                    }
                }
            }
        }
    }
}