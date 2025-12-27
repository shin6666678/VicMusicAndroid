package com.shin.vicmusic.feature.playlist.meList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.PLAYLISTS
import com.shin.vicmusic.feature.common.CreatePlaylistDialog
import com.shin.vicmusic.feature.common.ItemPlaylist
import com.shin.vicmusic.feature.playlist.detail.navigateToPlaylistDetail

@Preview
@Composable
fun PreView() {
    MyPlaylistScreen(
        playlists = PLAYLISTS,
        onPlaylistClick = {}
    )
}

@Composable
fun MyPlaylistRoute(
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val playlists by viewModel.playlists.collectAsState()
    val navController = LocalNavController.current
    LaunchedEffect(Unit) {
        viewModel.fetchMyPlaylists()
    }

    MyPlaylistScreen(
        playlists = playlists,
        onPlaylistClick = navController::navigateToPlaylistDetail,
        onBackClick = navController::popBackStack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlaylistScreen(
    playlists: List<Playlist>,
    onPlaylistClick: (String) -> Unit,
    onBackClick: () -> Unit = {},
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("自建歌单", "收藏歌单")

    // 弹窗逻辑
    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
        )
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    title = {
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            indicator = {},
                            divider = {}
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                ) {
                                    Text(
                                        text = title,
                                        style = if (selectedTabIndex == index)
                                            MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                        else
                                            MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${playlists.size}张歌单", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.weight(1f))
                    // 点击触发弹窗
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "新建歌单")
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.List, contentDescription = "List")
                    }
                }
            }
            items(playlists) { playlist ->
                ItemPlaylist(playlist = playlist, onClick = { onPlaylistClick(playlist.id) })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

