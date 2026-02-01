package com.shin.vicmusic.feature.common.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.core.domain.ChatSession
import com.shin.vicmusic.core.model.api.NotifyDto
import com.shin.vicmusic.feature.common.MyAsyncImage

@Composable
fun ItemNotify(
    notify: NotifyDto,
    onClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(notify.title, fontWeight = FontWeight.Bold)
            Text(notify.content)
        }
    }
}
@Composable
fun ItemChat(
    session: ChatSession,
    onClick: (ChatSession) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {onClick(session)},
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MyAsyncImage(
            modifier = Modifier.size(50.dp),
            model = session.avatar,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = session.username, fontWeight = FontWeight.Bold)
            Text(
                text = session.lastMessage ?: "",
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = session.lastTime.toString(), fontSize = 10.sp, color = Color.Gray)
            if (session.unreadCount > 0) {
                Badge { Text(text = session.unreadCount.toString()) }
            }
        }
    }
}