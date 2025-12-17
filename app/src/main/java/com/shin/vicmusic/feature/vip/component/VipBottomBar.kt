package com.shin.vicmusic.feature.vip.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.feature.vip.VipBlackBg
import com.shin.vicmusic.feature.vip.VipGold

@Composable
fun VipBottomBar(price: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(VipBlackBg)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Button(
            onClick = { /* TODO: 支付逻辑 */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VipGold
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(
                text = "立即开通 $price",
                color = Color(0xFF4E342E),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}