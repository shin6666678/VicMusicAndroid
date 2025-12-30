package com.shin.vicmusic.feature.search.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun SearchTopBarPreview() {
    SearchTopBar(
        searchText = "",
        onSearchTextChange = {},
        onBackClick = {},
        onMicClick = {},
        onSearch = {},
        onClear = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onMicClick: () -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    modifier = Modifier
                        .weight(1f),
                    placeholder = {
                        Text(
                            "搜索歌曲、歌单、歌手...",
                            fontSize = 14.sp,
                            maxLines = 1 // 防止提示文案换行
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = onClear) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.Gray
                                )
                            }
                        } else {
                            IconButton(onClick = onMicClick) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Microphone",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(25.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = Color(0xFFF0F0F0),
                        unfocusedContainerColor = Color(0xFFF0F0F0)
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch() })
                )

                TextButton(onClick = onSearch) {
                    Text("搜索", color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}