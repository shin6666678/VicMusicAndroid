package com.shin.vicmusic.feature.common.dialog

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size // 记得导入 size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicVideo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.R
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.design.theme.LocalAppColors // 1. 导入自定义颜色

@Composable
fun CopyrightDialog(
    song: Song?,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current

    AlertDialog(
        containerColor = colors.gradientEnd,
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "版权提示",
                color = colors.textColor
            )
        },
        text = {
            Column {
                Text(
                    text = song?.disclaimer?.takeIf { it.isNotBlank() } ?: "抱歉，由于版权原因，该歌曲暂不支持播放。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textColor.copy(alpha = 0.8f) // 稍作减淡，区分标题
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "您可以前往以下平台收听：",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textColor.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                song?.externalUrls?.forEach { externalUrl ->
                    val painter = when {
                        externalUrl.name.contains("网易云") -> R.drawable.ic_logo_net_easy
                        externalUrl.name.contains("QQ") -> R.drawable.ic_music_logo
                        externalUrl.name.contains("Spotify") -> R.drawable.ic_music_logo
                        else -> R.drawable.ic_music_logo
                    }
                    ListItem(
                        headlineContent = {
                            Text(
                                text = externalUrl.name,
                                color = colors.textColor
                            )
                        },
                        leadingContent = {
                            Image(
                                painter = painterResource(id = painter),
                                contentDescription = externalUrl.name,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        modifier = Modifier.clickable {
                            try {
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse(externalUrl.url)
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Log.e("CopyrightDialog", "Open URL failed", e)
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = "继续在本地探索",
                    color = colors.accentPrimary
                )
            }
        }
    )
}