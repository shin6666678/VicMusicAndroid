package com.shin.vicmusic.feature.me.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shin.vicmusic.feature.me.ActionItem

@Composable
fun UserInfoCard(onAvatarClick: () -> Unit = {}){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable(onClick = onAvatarClick) // 添加点击事件
                ) {
                    AsyncImage(
                        model = "https://picsum.photos/64/64?random=2", // Placeholder for avatar
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "本地用户",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "VIP6",
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .background(Color(0xFF00BFA5), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "爵士V1",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .background(Color.LightGray, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "19级",
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .background(Color(0xFFFF4081), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "2", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "关注", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ActionItem(icon = Icons.Filled.Star, text = "提现", iconTint = Color(0xFFFF9800))
                ActionItem(icon = Icons.Filled.Favorite, text = "会员", iconTint = Color(0xFF00BFA5))
                ActionItem(icon = Icons.Filled.ShoppingCart, text = "装扮", iconTint = Color(0xFF2196F3))
                ActionItem(icon = Icons.Filled.DateRange, text = "日签", iconTint = Color(0xFFF44336))
            }
        }
    }
}