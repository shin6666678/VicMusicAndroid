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
                 modifier: Modifier): Unit {
    if(model==""||model==null)
        Image(
            painter = painterResource(id = R.drawable.ic_launcher),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(extraSmallRoundedCornerShape)
            //.clip(RoundedCornerShape(5.dp))
        )
    else
        AsyncImage(
            model = ResourceUtil.r2(model),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(MaterialTheme.shapes.small)
                .background(LocalDividerColor.current)
        )
}