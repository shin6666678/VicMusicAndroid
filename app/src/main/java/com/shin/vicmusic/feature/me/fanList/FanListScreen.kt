package com.shin.vicmusic.feature.me.fanList

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.feature.common.CommonTopBar
import com.shin.vicmusic.feature.common.MyAsyncImage

@Composable
fun FanListScreen(
    viewModel: FanListViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current

    LaunchedEffect(Unit) { viewModel.loadData() }

    Scaffold(
        topBar = {
            CommonTopBar(
                midText = "我的粉丝",
                popBackStack = { navController.popBackStack() })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (viewModel.isLoading) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if (viewModel.userList.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("没有粉丝")
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
                            Text(user.name, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}