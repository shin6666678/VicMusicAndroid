package com.shin.vicmusic.feature.common.dialog

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.domain.Song

@Composable
fun CopyrightDialog(
    song: Song?,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("版权提示") },
        text = {
            Column {
                Text(
                    text = song?.disclaimer?.takeIf { it.isNotBlank() } ?: "抱歉，由于版权原因，该歌曲暂不支持直接播放。",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "您可以前往以下平台收听：", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                song?.externalUrls?.forEach { externalUrl ->
                    val icon = when {
                        externalUrl.name.contains("网易云") -> Icons.Default.MusicNote
                        externalUrl.name.contains("QQ") -> Icons.Default.MusicVideo
                        externalUrl.name.contains("Spotify") -> Icons.Default.Headset
                        else -> Icons.Default.MusicNote
                    }
                    ListItem(
                        headlineContent = { Text(externalUrl.name) },
                        leadingContent = {
                            Icon(
                                imageVector = icon,
                                contentDescription = externalUrl.name,
                                tint = MaterialTheme.colorScheme.primary
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
            TextButton(onClick = onDismissRequest) { Text("继续在本地探索") }
        }
    )
}
