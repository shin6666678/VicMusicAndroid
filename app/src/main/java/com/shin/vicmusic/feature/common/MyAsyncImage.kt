package com.shin.vicmusic.feature.common

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.shin.vicmusic.R
import com.shin.vicmusic.util.ResourceUtil


@Composable
fun MyAsyncImage(model: String?,
                 modifier: Modifier,
                 contentScale: ContentScale = ContentScale.Crop): Unit {
    if(model==""||model==null)
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier,

        )
    else
        AsyncImage(
            model = ResourceUtil.r2(model),
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier,
            placeholder = painterResource(id = R.drawable.ic_disc), // 替换为你的默认封面
            error = painterResource(id = R.drawable.logo) // 加载失败时显示
        )
}