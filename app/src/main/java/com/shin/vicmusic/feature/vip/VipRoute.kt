package com.shin.vicmusic.feature.vip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shin.vicmusic.feature.vip.component.VipBottomBar
import com.shin.vicmusic.feature.vip.component.VipPrivilegesSection
import com.shin.vicmusic.feature.vip.component.VipProductList
import com.shin.vicmusic.feature.vip.component.VipTopBar
import com.shin.vicmusic.feature.vip.component.VipUserCard

@Preview
@Composable
fun VipRoutePreview() {
    VipScreen(onBackClick = {})
}

@Composable
fun VipRoute(onBackClick: () -> Unit) {
    VipScreen(onBackClick = onBackClick)
}

// 定义一些VIP页面专用的颜色
val VipBlackBg = Color(0xFF141414)
val VipCardBg = Color(0xFF2A2A2A)
val VipGold = Color(0xFFE3C598)
private val VipGoldDark = Color(0xFFC49F6D)
val VipLightText = Color(0xFFF5F5F5)
val VipSubText = Color(0xFF9E9E9E)

@Composable
fun VipScreen(
    onBackClick: () -> Unit,
    viewModel: VipViewModel = viewModel()
) {
    val vipProducts by viewModel.vipProducts.collectAsState()
    val selectedIndex by viewModel.selectedProductIndex.collectAsState()

    Scaffold(
        containerColor = VipBlackBg,
        topBar = {
            VipTopBar(onBackClick)
        },
        bottomBar = {
            VipBottomBar(
                price = vipProducts.getOrNull(selectedIndex)?.price ?: "--"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 用户信息卡片
            VipUserCard()

            Spacer(modifier = Modifier.height(24.dp))

            // 会员套餐选择
            Text(
                text = "会员套餐",
                color = VipLightText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            VipProductList(
                products = vipProducts,
                selectedIndex = selectedIndex,
                onSelect = { viewModel.selectProduct(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 会员权益
            Text(
                text = "会员权益",
                color = VipLightText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            VipPrivilegesSection()

            Spacer(modifier = Modifier.height(100.dp)) // 底部留白
        }
    }
}







