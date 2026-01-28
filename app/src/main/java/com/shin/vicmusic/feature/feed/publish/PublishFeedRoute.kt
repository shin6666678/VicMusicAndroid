package com.shin.vicmusic.feature.feed.publish

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishFeedRoute(
    onBack: () -> Unit={},
    viewModel: PublishFeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var comment by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isPublished) {
        if (uiState.isPublished) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("发布动态") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(onClick = { viewModel.publishFeed(comment) }) {
                        Text("发布")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            TextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("分享你的新鲜事...") },
                maxLines = 10
            )
        }
    }
}
