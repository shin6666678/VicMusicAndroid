package com.shin.vicmusic.feature.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.ChatMessage

@Composable
fun ChatRoute(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val listState = rememberLazyListState()

    val messages by viewModel.messagesFlow.collectAsStateWithLifecycle()

    ChatScreen(
        listState = listState,
        messages = messages,
        targetUserName = viewModel.targetUserName,
        currentUserId = viewModel.currentUserId,
        inputText = viewModel.inputText,
        onInputChange = { viewModel.inputText = it },
        onSendClick = { viewModel.send() },
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    listState: LazyListState,
    messages: List<ChatMessage>,
    targetUserName: String,
    currentUserId: String,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(targetUserName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("输入消息...") }
                )
                IconButton(onClick = onSendClick) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { msg ->
                MessageBubble(msg = msg, currentUserId = currentUserId)
            }
        }
    }
}

@Composable
fun MessageBubble(msg: ChatMessage, currentUserId: String) {
    val isMe = msg.fromUserId == currentUserId

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val maxBubbleWidth = screenWidth * 0.75f

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
    ) {
        Surface(
            color = if (isMe) MaterialTheme.colorScheme.primary else Color.LightGray,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .widthIn(max = maxBubbleWidth)
        ) {
            Text(
                text = msg.content,
                modifier = Modifier.padding(10.dp),
                color = if (isMe) Color.White else Color.Black
            )
        }
    }
}