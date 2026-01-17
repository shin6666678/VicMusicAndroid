package com.shin.vicmusic.feature.common.bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun CommonTopBarPreview() {
    CommonTopBar(midText = "维克音乐排行榜")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopBar(
    midText: String = "",
    showSearch: Boolean = false,
    popBackStack: () -> Unit = {},
    navigateToSearch: () -> Unit = {},
    containerColor: Color = Color.Transparent,
    contentColor: Color = Color.Black
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor,
            actionIconContentColor = contentColor
        ),
        title = {
            Text(
                text = midText,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        navigationIcon = {
            IconButton(onClick = { popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (showSearch)
                IconButton(onClick = { navigateToSearch() }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }
        },
    )
}