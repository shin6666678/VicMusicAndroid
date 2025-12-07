package com.shin.vicmusic.feature.me.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeTopBar(){
    TopAppBar(
        title = {
            Text(
                text = "我的",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        actions = {
            Icon(
                painter = painterResource(android.R.drawable.ic_dialog_map), // Placeholder for island icon
                contentDescription = "Island",
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            BadgedBox(badge = { Badge { Text("14") } }) {
                Icon(
                    imageVector = Icons.Filled.MailOutline,
                    contentDescription = "Messages",
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Filled.Tune, // Placeholder for settings icon
                contentDescription = "Settings",
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}