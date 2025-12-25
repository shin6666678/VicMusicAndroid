package com.shin.vicmusic.feature.me.followList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.feature.common.CommonTopBar
import com.shin.vicmusic.feature.common.MyAsyncImage

@Composable
fun FollowListScreen(
    viewModel: FollowListViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current

    LaunchedEffect(Unit) { viewModel.loadData() }

    Scaffold(
        topBar = { CommonTopBar(midText = "我的关注(My Follows)") }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = viewModel.tabIndex) {
                Tab(selected = viewModel.tabIndex == 0, onClick = { viewModel.switchTab(0) }, text = { Text("用户(Users)") })
                Tab(selected = viewModel.tabIndex == 1, onClick = { viewModel.switchTab(1) }, text = { Text("歌手(Artists)") })
            }

            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if (viewModel.tabIndex == 0) {
                        items(viewModel.userList) { user ->
                            Row(modifier = Modifier.fillMaxWidth().clickable { /* 预留跳转(TODO Jump) */ }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                MyAsyncImage(model = user.headImg, modifier = Modifier.size(50.dp).clip(CircleShape))
                                Spacer(Modifier.width(16.dp))
                                Text(user.name?: "", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    } else {
                        items(viewModel.artistList) { artist ->
                            Row(modifier = Modifier.fillMaxWidth().clickable { /* 预留跳转(TODO Jump) */ }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                MyAsyncImage(model = artist.image, modifier = Modifier.size(50.dp).clip(CircleShape))
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(artist.name, style = MaterialTheme.typography.titleMedium)
                                    Text(artist.description, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}