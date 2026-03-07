package com.shin.vicmusic.feature.auth

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.R
import com.shin.vicmusic.core.design.composition.LocalNavController
import kotlinx.coroutines.flow.collectLatest

// ---- 主题色常量 ----
private val GradientStart = Color(0xFF020617)
private val GradientMid = Color(0xFF0F172A)
private val GradientEnd = Color(0xFF1E293B)
private val AccentPrimary = Color(0xFF3B82F6)
private val AccentSecondary = Color(0xFF2DD4BF)
private val GlassWhite = Color(0x1AFFFFFF)
private val GlassBorder = Color(0x33FFFFFF)
private val InputBackground = Color(0x0DFFFFFF)

@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 收集一次性副作用
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is LoginEffect.NavigateToMain -> {
                    Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                is LoginEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        onIntent = viewModel::processIntent,
        onRegisterClick = { navController.navigateToRegister() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
    onRegisterClick: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    // ---- 入场动画 ----
    val contentAlpha = remember { Animatable(0f) }
    val contentSlide = remember { Animatable(60f) }
    val logoScale = remember { Animatable(0.3f) }

    LaunchedEffect(Unit) {
        logoScale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        contentAlpha.animateTo(1f, animationSpec = tween(600))
        contentSlide.animateTo(0f, animationSpec = tween(600, easing = FastOutSlowInEasing))
    }

    // ---- 浮动光晕动画 ----
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glow1X by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow1x"
    )
    val glow2X by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow2x"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(GradientStart, GradientMid, GradientEnd))
            )
    ) {
        // ---- 背景光晕球 ----
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (glow1X * 200 - 100).dp, y = (-50).dp)
                .blur(80.dp)
                .background(
                    Brush.radialGradient(
                        listOf(AccentPrimary.copy(alpha = 0.35f), Color.Transparent),
                        center = Offset.Zero, radius = 600f
                    ),
                    shape = RoundedCornerShape(50)
                )
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (glow2X * 80).dp, y = (-(glow2X * 60)).dp)
                .blur(70.dp)
                .background(
                    Brush.radialGradient(
                        listOf(AccentSecondary.copy(alpha = 0.3f), Color.Transparent),
                        center = Offset.Zero, radius = 500f
                    ),
                    shape = RoundedCornerShape(50)
                )
        )

        // ---- 主内容 ----
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo 区域
            Box(
                modifier = Modifier
                    .scale(logoScale.value * pulse)
                    .size(90.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(AccentPrimary, AccentSecondary),
                            start = Offset(0f, 0f), end = Offset(300f, 300f)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(64.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 标题
            Text(
                text = "VicMusic",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.alpha(contentAlpha.value)
            )
            Text(
                text = "探索无限音乐世界",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier
                    .alpha(contentAlpha.value)
                    .padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ---- 玻璃拟态卡片 ----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = contentSlide.value.dp)
                    .alpha(contentAlpha.value)
                    .clip(RoundedCornerShape(24.dp))
                    .background(GlassWhite)
                    .padding(1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x26FFFFFF), Color(0x0DFFFFFF))
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "欢迎登录",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 邮箱输入框
                    AuthTextField(
                        value = uiState.mail,
                        onValueChange = { onIntent(LoginIntent.UpdateMail(it)) },
                        label = "邮箱账号",
                        leadingIcon = {
                            Icon(Icons.Default.Email, null, tint = AccentPrimary.copy(alpha = 0.8f))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 密码输入框
                    AuthTextField(
                        value = uiState.password,
                        onValueChange = { onIntent(LoginIntent.UpdatePassword(it)) },
                        label = "登录密码",
                        leadingIcon = {
                            Icon(Icons.Default.Lock, null, tint = AccentPrimary.copy(alpha = 0.8f))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 登录按钮
                    Button(
                        onClick = { onIntent(LoginIntent.Login) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (!uiState.isLoading)
                                        Brush.linearGradient(
                                            listOf(AccentPrimary, AccentSecondary),
                                            start = Offset(0f, 0f),
                                            end = Offset(800f, 0f)
                                        )
                                    else
                                        Brush.linearGradient(
                                            listOf(Color(0x66FFFFFF), Color(0x33FFFFFF))
                                        )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "立即登录",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 注册入口
            Row(
                modifier = Modifier.alpha(contentAlpha.value),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("还没有账号？", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                TextButton(onClick = onRegisterClick, contentPadding = PaddingValues(horizontal = 6.dp)) {
                    Text(
                        "立即注册",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentPrimary
                    )
                }
            }
        }
    }
}

/** 统一风格的输入框组件 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = AccentPrimary,
            unfocusedBorderColor = GlassBorder,
            focusedContainerColor = InputBackground,
            unfocusedContainerColor = InputBackground,
            cursorColor = AccentPrimary,
            focusedLabelColor = AccentPrimary,
        )
    )
}