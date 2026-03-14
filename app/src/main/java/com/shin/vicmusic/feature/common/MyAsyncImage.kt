package com.shin.vicmusic.feature.common

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.shin.vicmusic.R
import com.shin.vicmusic.util.ResourceUtil
import java.io.File


@Composable
fun MyAsyncImage(model: String?,
                 modifier: Modifier,
                 contentScale: ContentScale = ContentScale.Crop) {
    if (model.isNullOrEmpty()) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier,
        )
    } else {
        val imageModel: Any = if (model.startsWith("/")) {
            File(model)
        } else {
            ResourceUtil.r2(model)
        }
        AsyncImage(
            model = imageModel,
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier,
            placeholder = painterResource(id = R.drawable.ic_disc),
            error = painterResource(id = R.drawable.logo)
        )
    }
}