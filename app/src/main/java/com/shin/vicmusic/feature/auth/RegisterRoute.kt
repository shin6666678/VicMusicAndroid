package com.shin.vicmusic.feature.auth

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import com.shin.vicmusic.core.design.theme.isAppInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.shin.vicmusic.R
import com.shin.vicmusic.core.design.composition.LocalNavController
import kotlinx.coroutines.flow.collectLatest
import com.shin.vicmusic.core.design.theme.LocalAppColors


@Composable
fun RegisterRoute(
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 收集一次性副作用
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RegisterEffect.NavigateBack -> {
                    Toast.makeText(context, "注册成功，请登录", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                is RegisterEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    RegisterScreen(
        uiState = uiState,
        onIntent = viewModel::processIntent,
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onIntent: (RegisterIntent) -> Unit,
    onBackClick: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // ---- 入场动画 ----
    val contentAlpha = remember { Animatable(0f) }
    val contentSlide = remember { Animatable(60f) }
    val logoScale = remember { Animatable(0.3f) }

    // 表单切换动画：发送验证码前 vs 发送验证码后
    val formTransition = updateTransition(targetState = uiState.emailCodeSent, label = "formTransition")
    
    val heightAnim by formTransition.animateDp(
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow) },
        label = "heightAnim"
    ) { sent ->
        if (sent) 480.dp else 400.dp
    }


    LaunchedEffect(Unit) {
        logoScale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        contentAlpha.animateTo(1f, animationSpec = tween(600))
        contentSlide.animateTo(0f, animationSpec = tween(600, easing = FastOutSlowInEasing))
    }

    val textColor = LocalAppColors.current.textColor
    val accentPrimary = LocalAppColors.current.accentPrimary
    val accentSecondary = LocalAppColors.current.accentSecondary

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
                Brush.verticalGradient(
                    listOf(
                        LocalAppColors.current.gradientStart,
                        LocalAppColors.current.gradientMid,
                        LocalAppColors.current.gradientEnd
                    )
                )
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
                        listOf(accentPrimary.copy(alpha = 0.35f), Color.Transparent),
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
                        listOf(accentSecondary.copy(alpha = 0.3f), Color.Transparent),
                        center = Offset.Zero, radius = 500f
                    ),
                    shape = RoundedCornerShape(50)
                )
        )

        // ---- 顶部返回按钮 ----
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .alpha(contentAlpha.value)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
        }

        // ---- 主内容 ----
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 中心图标区域
            Box(
                modifier = Modifier
                    .scale(logoScale.value * pulse)
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(accentPrimary, accentSecondary),
                            start = Offset(0f, 0f), end = Offset(300f, 300f)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PersonAdd,
                    contentDescription = "Register",
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 标题
            Text(
                text = "创建账号",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor,
                modifier = Modifier.alpha(contentAlpha.value)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // ---- 玻璃拟态卡片 ----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightAnim)
                    .offset(y = contentSlide.value.dp)
                    .alpha(contentAlpha.value)
                    .clip(RoundedCornerShape(24.dp))
                    .background(LocalAppColors.current.glassWhite)
                    .padding(1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(LocalAppColors.current.glassBorder.copy(alpha = 0.15f), LocalAppColors.current.inputBackground.copy(alpha = 0.05f))
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    
                    if (!uiState.emailCodeSent) {
                        // ==== 阶段 1：填写邮箱和验证码 ====
                        Text(
                            text = "第一步：验证邮箱",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor.copy(alpha = 0.9f),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        // 邮箱输入框
                        AuthTextField(
                            value = uiState.email,
                            onValueChange = { onIntent(RegisterIntent.UpdateEmail(it)) },
                            label = "邮箱地址",
                            leadingIcon = {
                                Icon(Icons.Default.Email, null, tint = accentPrimary.copy(alpha = 0.8f))
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 图形验证码
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                AuthTextField(
                                    value = uiState.captcha,
                                    onValueChange = { onIntent(RegisterIntent.UpdateCaptcha(it)) },
                                    label = "图形验证码",
                                    leadingIcon = {
                                        Icon(Icons.Default.Image, null, tint = accentPrimary.copy(alpha = 0.8f))
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Done
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .size(110.dp, 56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .clickable { onIntent(RegisterIntent.RefreshCaptcha) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.captchaImageUrl != null) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(uiState.captchaImageUrl)
                                            .diskCachePolicy(CachePolicy.DISABLED)
                                            .memoryCachePolicy(CachePolicy.DISABLED)
                                            .build(),
                                        contentDescription = "验证码",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = accentPrimary,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // 发送验证码按钮
                        Button(
                            onClick = { onIntent(RegisterIntent.SendEmailCode) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            enabled = !uiState.emailCodeSending,
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
                                        if (!uiState.emailCodeSending)
                                            Brush.linearGradient(
                                                listOf(accentPrimary, accentSecondary),
                                                start = Offset(0f, 0f), end = Offset(800f, 0f)
                                            )
                                        else
                                            Brush.linearGradient(
                                                listOf(Color(0x66FFFFFF), Color(0x33FFFFFF))
                                            )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.emailCodeSending) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("发送邮箱验证码", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                    } else {
                        // ==== 阶段 2：填写收到的验证码和密码 ====
                        Text(
                            text = "第二步：设置密码",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor.copy(alpha = 0.9f),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "验证码已发送至 ${uiState.email}",
                            fontSize = 12.sp,
                            color = accentSecondary.copy(alpha = 0.9f),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // 邮箱验证码
                        AuthTextField(
                            value = uiState.mailCode,
                            onValueChange = { onIntent(RegisterIntent.UpdateMailCode(it)) },
                            label = "邮箱收到的验证码",
                            leadingIcon = {
                                Icon(Icons.Default.Pin, null, tint = accentPrimary.copy(alpha = 0.8f))
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 密码
                        AuthTextField(
                            value = uiState.password,
                            onValueChange = { onIntent(RegisterIntent.UpdatePassword(it)) },
                            label = "设置密码",
                            leadingIcon = {
                                Icon(Icons.Default.Lock, null, tint = accentPrimary.copy(alpha = 0.8f))
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = null,
                                        tint = textColor.copy(alpha = 0.5f)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            )
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // 注册按钮
                        Button(
                            onClick = { onIntent(RegisterIntent.Register) },
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
                                                listOf(accentPrimary, accentSecondary),
                                                start = Offset(0f, 0f), end = Offset(800f, 0f)
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
                                        modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("立即注册", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 底部提示信息
            Text(
                text = "注册即代表您同意 VicMusic 的服务条款和隐私权政策",
                color = textColor.copy(alpha = 0.4f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(contentAlpha.value)
                    .padding(horizontal = 24.dp)
            )
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
    val textColor = LocalAppColors.current.textColor
    val accentPrimary = LocalAppColors.current.accentPrimary
    val glassBorder = LocalAppColors.current.glassBorder
    val inputBg = LocalAppColors.current.inputBackground

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = textColor.copy(alpha = 0.5f), fontSize = 13.sp) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedBorderColor = accentPrimary,
            unfocusedBorderColor = glassBorder,
            focusedContainerColor = inputBg,
            unfocusedContainerColor = inputBg,
            cursorColor = accentPrimary,
            focusedLabelColor = accentPrimary,
        )
    )
}