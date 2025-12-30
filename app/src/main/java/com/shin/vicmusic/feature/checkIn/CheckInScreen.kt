package com.shin.vicmusic.feature.checkIn

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.feature.common.CommonTopBar

// 定义符合主流APP的高级浅色调配色方案
// 浅灰白背景，干净清爽
private val AppBackground = Color(0xFFF5F6F8)
// 尊贵深蓝 (用于卡片主色)
private val PremiumDarkBlue = Color(0xFF2C3E50)
// 活力靛青 (用于渐变和按钮)
private val VividIndigo = Color(0xFF3F51B5)
// 柔和灰 (用于次要文字)
private val TextSecondary = Color(0xFF909399)
// 浅黑 (用于标题)
private val TextPrimary = Color(0xFF303133)
// 纯白
private val White = Color.White

@Preview
@Composable
fun CheckInScreenPreview() {
    CheckInScreen(
        onCheckInClick = {},
        points = 8848,
        isLoading = false
    )
}

@Composable
fun CheckInRoute(
    viewModel: CheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val navController = LocalNavController.current

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is CheckInUiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }

            is CheckInUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }

            else -> {}
        }
    }

    CheckInScreen(
        onCheckInClick = viewModel::checkIn,
        points = currentUser?.points ?: 0,
        isLoading = uiState is CheckInUiState.Loading,
        onBackClick = navController::popBackStack
    )
}

@Composable
fun CheckInScreen(
    onBackClick: () -> Unit = {},
    onCheckInClick: () -> Unit,
    points: Int,
    isLoading: Boolean
) {
    Scaffold(
        topBar = {
            CommonTopBar(midText = "签到中心", popBackStack = onBackClick)
        },
        containerColor = AppBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 1. 顶部会员积分卡 (类似信用卡的质感)
            MembershipCard(points = points)

            Spacer(modifier = Modifier.weight(1f))

            // 2. 核心签到按钮区域 (带有呼吸效果)
            CheckInButtonArea(
                onCheckInClick = onCheckInClick,
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.weight(1f))

            // 3. 规则说明区域 (极简风格)
            CheckInRules()

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun MembershipCard(points: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = VividIndigo.copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PremiumDarkBlue, VividIndigo),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                    )
                )
        ) {
            // 卡片背景装饰纹理
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-30).dp)
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(White.copy(alpha = 0.05f))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-20).dp, y = 40.dp)
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(White.copy(alpha = 0.05f))
            )

            // 卡片内容
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700), // 经典金
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "尊享会员",
                        style = MaterialTheme.typography.titleMedium,
                        color = White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Column {
                    Text(
                        text = "当前可用积分",
                        style = MaterialTheme.typography.labelMedium,
                        color = White.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("%,d", points), // 数字格式化
                        style = MaterialTheme.typography.displayMedium,
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 42.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CheckInButtonArea(
    onCheckInClick: () -> Unit,
    isLoading: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ripple")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(200.dp)
    ) {
        // 呼吸光环 (仅在非加载状态显示)
        if (!isLoading) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(VividIndigo.copy(alpha = 0.1f))
            )
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale * 0.9f)
                    .clip(CircleShape)
                    .background(VividIndigo.copy(alpha = 0.15f))
            )
        }

        // 主按钮
        Button(
            onClick = onCheckInClick,
            enabled = !isLoading,
            modifier = Modifier
                .size(140.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    spotColor = VividIndigo.copy(alpha = 0.5f)
                ),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.LightGray
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isLoading) {
                            Brush.linearGradient(listOf(Color.LightGray, Color.Gray))
                        } else {
                            Brush.linearGradient(
                                colors = listOf(VividIndigo, PremiumDarkBlue),
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(0f, 400f)
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = White,
                        trackColor = White.copy(alpha = 0.3f),
                        strokeWidth = 3.dp
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "签到",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Text(
                            text = "Check In",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CheckInRules() {
    Surface(
        color = White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(VividIndigo)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "规则说明",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            SimpleRuleItem("每日签到可获得随机积分奖励")
            SimpleRuleItem("连续签到7天可获得额外加成")
            SimpleRuleItem("积分可用于商城兑换或抵扣")
        }
    }
}

@Composable
fun SimpleRuleItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = TextSecondary,
            modifier = Modifier.padding(end = 8.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            lineHeight = 20.sp
        )
    }
}