package com.shin.vicmusic.feature.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.theme.LocalDividerColor
import com.shin.vicmusic.core.design.theme.SpaceExtraMedium
import com.shin.vicmusic.core.design.theme.SpaceOuter

/**
 * 发现顶部标题栏
 */
@Composable
fun DiscoveryTopBar(
    toSearch: () -> Unit,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("推荐", "乐馆")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(top = 16.dp, bottom = 8.dp) // 根据图片调整顶部栏的内边距
    ) {
        // 顶部导航文本（推荐、乐馆、听书等）
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = SpaceOuter), // 两端留白
            horizontalArrangement = Arrangement.spacedBy(24.dp), // [关键] 固定间距，不再平分
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(tabs) { index, tabText ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tabText,
                        style = MaterialTheme.typography.titleLarge, // 调整字体大小以匹配图片
                        color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.clickable { onTabSelected(index) }
                    )
                    if (selectedTabIndex == index) { // 根据 selectedTabIndex 来显示下划线
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .width(24.dp) // 下划线的宽度
                                .height(2.dp) // 下划线的高度
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(1.dp)) // 下划线颜色和圆角
                        )
                    }
                }
            }
        }

        // 搜索框区域
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceOuter)
                .padding(top = 16.dp), // 调整搜索框与上方标签的间距
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
                    text = " 逆战 搜穿越火线的人常搜", // 文本内容
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
}