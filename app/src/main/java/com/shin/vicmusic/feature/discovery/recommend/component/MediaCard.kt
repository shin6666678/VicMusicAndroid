package com.shin.vicmusic.feature.discovery.recommend.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.shin.vicmusic.core.design.theme.SpaceMedium
import com.shin.vicmusic.core.design.theme.SpaceOuter
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.feature.discovery.recommend.MediaCardData

@Composable
fun HorizontalMediaCards(
    mediaCards: List<MediaCardData>,
    onMediaCardClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = SpaceOuter),
        horizontalArrangement = Arrangement.spacedBy(SpaceMedium)
    ) {
        items(mediaCards) { card ->
            MediaCard(data = card, onClick = { onMediaCardClick(card.id) })
        }
    }
}

@Composable
fun MediaCard(data: MediaCardData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(width = 150.dp, height = 200.dp) // Adjust size as per image
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(model = data.imageUrl),
                contentDescription = data.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceMedium),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = data.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = data.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
                if (data.isDaily) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF4CAF50).copy(alpha = 0.8f)) // Green overlay
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .align(Alignment.End)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Play Daily",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Daily 5", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }
                } else {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(8.dp)
                            .align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMediaCard() {
    VicMusicTheme {
        MediaCard(
            data = MediaCardData(
                id = "1",
                title = "下午茶",
                description = "此刻别无所求,只想...",
                imageUrl = "https://picsum.photos/200/300"
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDailyMediaCard() {
    VicMusicTheme {
        MediaCard(
            data = MediaCardData(
                id = "2",
                title = "Daily 30",
                description = "每日30首",
                imageUrl = "https://picsum.photos/200/300",
                isDaily = true
            ),
            onClick = {}
        )
    }
}