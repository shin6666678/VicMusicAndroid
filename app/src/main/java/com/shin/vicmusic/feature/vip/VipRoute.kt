package com.shin.vicmusic.feature.vip

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalAuthManager
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.feature.vip.component.VipBottomBar
import com.shin.vicmusic.feature.vip.component.VipPrivilegesSection
import com.shin.vicmusic.feature.vip.component.VipTopBar
import com.shin.vicmusic.feature.vip.component.VipUserCard

@Preview
@Composable
fun VipRoutePreview() {
    VipScreen(
        onBackClick = {},
        user = UserInfo(),
        onPurchaseClick = {}
    )
}

@Composable
fun VipRoute(
    viewModel: VipViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val authManager = LocalAuthManager.current
    val context = LocalContext.current

    // 收集 StateFlow 状态
    val isLoggedIn by authManager.isLoggedIn.collectAsState()

    // 检查登录状态
    if (isLoggedIn != true) {
        LaunchedEffect(Unit) {
            navController.navigate("login_route")
        }
        return
    }

    val loginUser by authManager.currentUser.collectAsState()

    // 监听 Toast 消息
    LaunchedEffect(Unit) {
        viewModel.message.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    VipScreen(
        onBackClick = navController::popBackStack,
        user = loginUser,
        onPurchaseClick = viewModel::purchaseVip
    )
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
    user: UserInfo?,
    onPurchaseClick: () -> Unit
) {
    val isVip = user?.isVip() == true

    Scaffold(
        containerColor = VipBlackBg,
        topBar = {
            VipTopBar(onBackClick)
        },
        bottomBar = {
            // 只有非VIP用户才显示购买栏
            if (!isVip) {
                Box(modifier = Modifier.navigationBarsPadding()) {
                    VipBottomBar(
                        onPurchaseClick = onPurchaseClick
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 用户信息卡片
            VipUserCard(user = user)

            Spacer(modifier = Modifier.height(46.dp))

            // 会员权益/我的特权
            Text(
                text = if (isVip) "我的特权" else "会员权益",
                color = VipLightText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            VipPrivilegesSection()

            // 如果是VIP(没有底部栏)，添加一个底部间距，防止内容贴底太紧
            if (isVip) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}