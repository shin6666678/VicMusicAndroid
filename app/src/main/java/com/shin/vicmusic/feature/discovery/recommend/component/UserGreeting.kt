package com.shin.vicmusic.feature.discovery.recommend.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.R
import com.shin.vicmusic.core.design.theme.SpaceExtraMedium
import com.shin.vicmusic.core.design.theme.SpaceMedium
import com.shin.vicmusic.core.design.theme.SpaceOuter
import com.shin.vicmusic.core.design.theme.VicMusicTheme

@Composable
fun UserGreeting() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpaceOuter, vertical = SpaceMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder avatar
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(SpaceMedium))
        Text(
            text = "上尉诗人",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(SpaceExtraMedium))
        // VIP6 Badge - Simplified for now
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF81C784)) // Light green
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(text = "VIP6", style = MaterialTheme.typography.labelSmall, color = Color.White)
        }
        Spacer(modifier = Modifier.width(SpaceExtraMedium))
        // Another badge - Simplified
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(text = "V1", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
        }
        Spacer(modifier = Modifier.weight(1f)) // Pushes content to the right
        Text(
            text = "1条新消息 >",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserGreeting() {
    VicMusicTheme {
        UserGreeting()
    }
}