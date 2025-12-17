package com.shin.vicmusic.feature.vip.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.R
import com.shin.vicmusic.feature.vip.VipCardBg
import com.shin.vicmusic.feature.vip.VipGold
import com.shin.vicmusic.feature.vip.VipSubText

@Composable
fun VipPrivilegesSection() {
    val privileges = listOf(
        "无损音质" to R.drawable.ic_disc, // 这里使用现有的图标，实际应替换为权益图标
        "免广告" to R.drawable.ic_theme,
        "鲸云音效" to R.drawable.ic_music_logo,
        "个性皮肤" to R.drawable.ic_mine
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        privileges.forEach { (name, icon) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(VipCardBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        // 暂时使用vectorResource如果找不到就用AsyncImage或者默认
                        // 这里为了演示直接用Icon，实际项目可能需要 Image(painterResource(id = icon)...)
                        painter = painterResource(id = icon),
                        contentDescription = name,
                        modifier = Modifier.size(24.dp),
                        tint = VipGold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = name,
                    color = VipSubText,
                    fontSize = 12.sp
                )
            }
        }
    }
}