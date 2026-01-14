package com.shin.vicmusic.feature.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.ChatSession
import com.shin.vicmusic.core.model.api.NotifyDto
import com.shin.vicmusic.feature.common.ItemChat
import com.shin.vicmusic.feature.common.ItemNotify
import com.shin.vicmusic.feature.common.bar.CommonTopBar

@Composable
fun MessageListRoute(
    viewModel: MessageListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navController = LocalNavController.current

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadChatSessions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    MessageListScreen(
        uiState = uiState,
        onBackClick = navController::popBackStack,
        onTabChange = viewModel::switchTab,
        onSessionClick = { session ->
            viewModel.clearUnread(session.userId)
            navController.navigate("chat/${session.userId}/${session.username}")
        }
    )
}

@Composable
fun MessageListScreen(
    uiState: MessageListUiState,
    onBackClick: () -> Unit,
    onTabChange: (Int) -> Unit,
    onSessionClick: (ChatSession) -> Unit // 添加這個回調參數
) {
    Scaffold(
        topBar = {
            Column {
                CommonTopBar(
                    midText = "消息中心",
                    popBackStack = onBackClick,
                )
                TabRow(selectedTabIndex = uiState.tabIndex) {
                    Tab(
                        selected = uiState.tabIndex == 0,
                        onClick = { onTabChange(0) },
                        text = { Text("通知") }
                    )
                    Tab(
                        selected = uiState.tabIndex == 1,
                        onClick = { onTabChange(1) },
                        text = { Text("私信") }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            if (uiState.tabIndex == 0) {
                NotificationList(uiState.notifications)
            } else {
                ChatSessionList(
                    list = uiState.chatSessions,
                    onClick = onSessionClick
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun NotificationList(list: List<NotifyDto>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(list) { msg ->
            ItemNotify(
                msg
            )
        }
    }
}

@Composable
fun ChatSessionList(
    list: List<ChatSession>,
    onClick: (ChatSession) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        items(list) { session ->
            ItemChat(
                session=session,
                onClick = { onClick(session) }
            )
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
        }
    }
}