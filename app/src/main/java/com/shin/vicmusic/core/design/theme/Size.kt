package com.shin.vicmusic.core.design.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

//region 分割线尺寸
val Space4XLarge=40.dp
val Space3XLarge=30.dp
val SpaceXLarge=20.dp
val SpaceOuter=16.dp
val SpaceExtraOuter=14.dp
val SpaceMedium=10.dp
val SpaceExtraMedium=7.dp
val SpaceSmall=5.dp
val SpaceExtraSmall=2.dp

@Composable
fun SpaceSmallHeight():Unit{
    Spacer(modifier = Modifier.height(SpaceSmall).fillMaxWidth())
}
@Composable
fun SpaceExtraSmallHeight():Unit{
    Spacer(
        modifier = Modifier
            .height(SpaceExtraSmall)
            .fillMaxWidth()
            .background(LocalDividerColor.current)
    )
}