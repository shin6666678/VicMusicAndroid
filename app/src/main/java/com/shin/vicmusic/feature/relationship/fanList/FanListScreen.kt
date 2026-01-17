package com.shin.vicmusic.feature.relationship.fanList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.feature.common.item.ItemUser

@Composable
fun FanListScreen(
    viewModel: FanListViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) { viewModel.loadData() }

    Column() {
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
                    ItemUser(
                        user = user,
                        showFollowStatus = true,
                    )
                }
            }
        }
    }

}