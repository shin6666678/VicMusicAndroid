package com.shin.vicmusic.core.design.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.shin.vicmusic.core.design.theme.VicMusicTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopAppBar(
    modifier: Modifier = Modifier,
    contentColor:Color = Color.Red
){
    var leftWidth by remember { mutableStateOf(1) }
    var rightWidth by remember { mutableStateOf(1) }

    Column(modifier = modifier) {
        TopAppBar(
            modifier = Modifier.fillMaxSize().height(88.dp).zIndex(1f),
            colors = TopAppBarDefaults.topAppBarColors(
                titleContentColor = contentColor
            ),
            title = {
                Box(modifier= Modifier.fillMaxSize()) {

                }
            }
        )
    }

}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VicMusicTheme {
        CommonTopAppBar()
    }
}