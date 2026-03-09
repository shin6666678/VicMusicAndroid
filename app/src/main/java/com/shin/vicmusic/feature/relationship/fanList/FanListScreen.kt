package com.shin.vicmusic.feature.relationship.fanList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shin.vicmusic.feature.common.state.ErrorRetryView
import com.shin.vicmusic.feature.common.item.ItemUser
import com.shin.vicmusic.feature.common.state.LoadingFooter

@Composable
fun FanListScreen(
    viewModel: FanListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val fanData = uiState.data.pageResult
    val fans = fanData.items

    LaunchedEffect(Unit) {
        if (fans.isEmpty()) viewModel.loadData()
    }

    Box(Modifier.fillMaxSize()) {
        if (uiState.isLoading && fans.isEmpty()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
        else if (uiState.errorMessage != null && fans.isEmpty()) {
            ErrorRetryView(
                message = uiState.errorMessage!!,
                onRetry = { viewModel.loadData() },
                modifier = Modifier.align(Alignment.Center)
            )
        }
        else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (fans.isEmpty() && !uiState.isLoading) {
                    item {  }
                } else {
                    items(fans) { user ->
                        ItemUser(user = user, showFollowStatus = true)
                    }

                    if (fanData.hasMore) {
                        item {
                            LaunchedEffect(Unit) {
                                viewModel.loadData()
                            }
                            if (uiState.isLoading) {
                                LoadingFooter()
                            }
                        }
                    }
                }
            }
        }
    }
}