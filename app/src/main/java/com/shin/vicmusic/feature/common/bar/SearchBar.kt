package com.shin.vicmusic.feature.common.bar

import android.R.attr.contentDescription
import android.R.attr.text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.theme.LocalDividerColor
import com.shin.vicmusic.core.design.theme.SpaceExtraMedium
import com.shin.vicmusic.core.design.theme.SpaceOuter

@Composable
fun SearchBar(
    toSearch: () -> Unit,
    placeHolderString: String = ""
){
    // 搜索框区域
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpaceOuter),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 左侧的logo和搜索输入框
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f) // 占据可用空间
                .height(38.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(LocalDividerColor.current) // 使用LocalDividerColor或者其他合适的背景色
                .clickable { toSearch() } // 点击时调用 toSearch 回调
                .padding(horizontal = SpaceExtraMedium) // 搜索框内部的内边距
        ) {
            // 左侧Logo (使用 PlayArrow 作为占位符，需要替换为实际的 `逆战` logo)
            Icon(
                imageVector = Icons.Default.PlayArrow, // TODO: 替换为实际的 `逆战` logo drawable
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = placeHolderString, // 文本内容
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // 右侧两个图标
        Row(
            modifier = Modifier.padding(start = SpaceExtraMedium), // 与搜索框的间距
            horizontalArrangement = Arrangement.spacedBy(SpaceExtraMedium) // 两个图标之间的间距
        ) {
            // 第一个右侧图标 (使用 AccountCircle 作为占位符，需要替换为实际的货币图标)
            Icon(
                imageVector = Icons.Default.AccountCircle, // TODO: 替换为实际的货币图标 drawable (R.drawable.ic_currency_yen)
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, // 根据图片，这个图标是橙色
                modifier = Modifier
                    .size(28.dp) // 调整图标大小
                    .clip(RoundedCornerShape(50.dp))
                    .background(MaterialTheme.colorScheme.surface) // 背景颜色
                    .padding(4.dp) // 图标内部边距
            )
            // 第二个右侧图标 (使用 MusicNote 作为占位符，需要替换为实际的音乐音符图标)
            Icon(
                imageVector = Icons.Default.MusicNote, // TODO: 替换为实际的音乐音符图标 drawable (R.drawable.ic_music_note)
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, // 根据图片，这个图标是橙色
                modifier = Modifier
                    .size(28.dp) // 调整图标大小
                    .clip(RoundedCornerShape(50.dp))
                    .background(MaterialTheme.colorScheme.surface) // 背景颜色
                    .padding(4.dp) // 图标内部边距
            )
        }
    }
}

@Composable
fun CommonSearchBar(
    toSearch: () -> Unit,
    placeHolderString: String = "",
    outModifier: Modifier= Modifier,
    inModifier: Modifier= Modifier,
    inImageVector: ImageVector=Icons.Default.Search
){
    // 搜索框区域
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpaceOuter),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 左侧的logo和搜索输入框
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f) // 占据可用空间
                .height(38.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(LocalDividerColor.current) // 使用LocalDividerColor或者其他合适的背景色
                .clickable { toSearch() }
                .padding(horizontal = SpaceExtraMedium) // 搜索框内部的内边距
        ) {
            // 左侧Logo
            Icon(
                imageVector = inImageVector,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = placeHolderString, // 文本内容
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}