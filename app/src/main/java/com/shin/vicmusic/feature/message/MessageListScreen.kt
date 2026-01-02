package com.shin.vicmusic.feature.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.R
import com.shin.vicmusic.core.model.api.ChatSessionDto
import com.shin.vicmusic.core.model.api.NotifyDto
import com.shin.vicmusic.feature.common.MyAsyncImage
import com.shin.vicmusic.feature.common.bar.CommonTopBar

@Composable
fun MessageListRoute(
    onBackClick: () -> Unit,
    viewModel: MessageListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MessageListScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onTabChange = viewModel::switchTab
    )
}

@Composable
fun MessageListScreen(
    uiState: MessageListUiState,
    onBackClick: () -> Unit,
    onTabChange: (Int) -> Unit
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
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.tabIndex == 0) {
                NotificationList(uiState.notifications)
            } else {
                ChatSessionList(uiState.chatSessions)
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun NotificationList(list: List<NotifyDto>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(list) { msg ->
            MessageItemCard(msg)
        }
    }
}

@Composable
fun ChatSessionList(list: List<ChatSessionDto>) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(list) { session ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyAsyncImage(
                    modifier = Modifier.size(50.dp),
                    model =  session.avatar,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = session.username, fontWeight = FontWeight.Bold)
                    Text(
                        text = session.lastMessage ?: "",
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = session.lastTime ?: "", fontSize = 10.sp, color = Color.Gray)
                    if (session.unreadCount > 0) {
                        Badge { Text(text = session.unreadCount.toString()) }
                    }
                }
            }
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
        }
    }
}

@Composable
fun MessageItemCard(notify: NotifyDto) {
    // ... (Use previous implementation) ...
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(notify.title, fontWeight = FontWeight.Bold)
            Text(notify.content)
        }
    }
}