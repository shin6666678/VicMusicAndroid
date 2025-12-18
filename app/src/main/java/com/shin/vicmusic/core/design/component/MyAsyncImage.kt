package com.shin.vicmusic.core.design.component

import android.R.attr.data
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shin.vicmusic.R
import com.shin.vicmusic.core.design.theme.LocalDividerColor
import com.shin.vicmusic.core.design.theme.extraSmallRoundedCornerShape
import com.shin.vicmusic.util.ResourceUtil


@Composable
fun MyAsyncImage(model: String?,
                 modifier: Modifier,
                 contentScale: ContentScale = ContentScale.Crop): Unit {
    if(model==""||model==null)
        Image(
            painter = painterResource(id = R.drawable.ic_launcher),
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
            placeholder = painterResource(id = R.drawable.ic_launcher), // 替换为你的默认封面
            error = painterResource(id = R.drawable.ic_launcher) // 加载失败时显示
        )
}