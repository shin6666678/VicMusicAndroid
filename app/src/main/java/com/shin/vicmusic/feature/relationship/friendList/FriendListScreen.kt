package com.shin.vicmusic.feature.relationship.friendList

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
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.feature.chat.navigateToChat
import com.shin.vicmusic.feature.common.ItemUser

@Composable
fun FriendListScreen(
    viewModel: FriendListViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current

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
                            Text("没有好友")
                        }
                    }
                } else items(viewModel.userList) { user ->
                    ItemUser(
                        user = user,
                        showSlogan = true,
                        showFollowStatus = true,
                        showPMButton = true,
                        onMessageClick = { // 实现回调
                            navController.navigateToChat(user.id, user.name)
                        }
                    )
                }
            }
        }
    }

}