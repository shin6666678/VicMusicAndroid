package com.shin.vicmusic.feature.common.bar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.core.design.composition.LocalSongActionManager
import com.shin.vicmusic.core.design.theme.LocalAppColors
import com.shin.vicmusic.core.domain.PayType
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.manager.PlayerUiState
import com.shin.vicmusic.feature.common.MyAsyncImage


@Composable
fun SongBar(
    song: Song?, // 接收 Song 对象
    playerUiState: PlayerUiState,
    onTogglePlayPause: () -> Unit,
    onPlaylistClick: () -> Unit,
    onBarClick: () -> Unit, // 添加整个bar的点击事件
    modifier: Modifier = Modifier
) {
    if(song == null)
         return
    val actionManager = LocalSongActionManager.current
    SongBarScreen(
        song = song, // 使用接收的 Song 对象
        playerUiState = playerUiState,
        onTogglePlayPause = onTogglePlayPause,
        onLikeClick = {actionManager.toggleLike(song)},
        onPlaylistClick = onPlaylistClick,
        onBarClick = onBarClick,
        modifier = modifier
    )
}

@Composable
fun SongBarScreen(
    song: Song?,
    playerUiState: PlayerUiState,
    onTogglePlayPause: () -> Unit,
    onLikeClick: () -> Unit,
    onPlaylistClick: () -> Unit,
    onBarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = LocalAppColors.current.glassWhite, // Use glass background instead of surfaceVariant
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp) // 根据设计图调整高度
            .clickable { onBarClick() }, // 整个bar可点击
        elevation = CardDefaults.cardElevation(0.dp) // Remove elevation to fix transparency shadow artifact
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 专辑封面 (左侧，带半圆露出)
            Box(modifier = Modifier.offset(x = (7).dp)) {
                MyAsyncImage(model = song?.icon, modifier = Modifier.size(50.dp))
            }

            // 歌曲信息
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song?.title ?: "未知歌曲",
                    style = MaterialTheme.typography.titleSmall,
                    color = LocalAppColors.current.textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = song?.artist?.name ?: "未知艺术家",
                        style = MaterialTheme.typography.labelSmall,
                        color = LocalAppColors.current.textColor.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    // VIP 标签
                    if (song != null && song.payType != PayType.FREE) {
                        if (song.payType == PayType.VIP) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFD4AF37), // 模拟VIP金色
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = "VIP",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        } else if (song.payType == PayType.PAY) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFD4AF37), // 模拟VIP金色
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = "付费",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }

                    }

                }
            }

            // 喜欢按钮
            IconButton(onClick = onLikeClick) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "喜欢",
                    tint = LocalAppColors.current.textColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 播放/暂停按钮
            IconButton(
                onClick = { onTogglePlayPause() },
            ) {
                Icon(
                    imageVector = if (playerUiState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (playerUiState.isPlaying) "暂停" else "播放",
                    tint = LocalAppColors.current.textColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            // 播放列表按钮
            IconButton(onClick = onPlaylistClick) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "播放列表",
                    tint = LocalAppColors.current.textColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
