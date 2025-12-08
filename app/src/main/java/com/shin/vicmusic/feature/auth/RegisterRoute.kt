package com.shin.vicmusic.feature.auth

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.feature.auth.RegisterViewModel

@Composable
@Preview(showBackground = true)
fun RegisterPreView(){
    RegisterScreen(email = "",
        onEmailChange = {},
        captcha = "",
        onCaptchaChange = {},
        captchaImageUrl = "https://via.placeholder.com/150",
        onCaptchaImageClick = {},
        onSendEmailCodeClick = {},
        emailCodeSentStatus = null,
        password = "",
        onPasswordChange = {},
        mailCode = "",
        onMailCodeChange = {},
        onRegisterClick = {}
    )
}

@Composable
fun RegisterRoute(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()) { // 使用 RegisterViewModel

    // 收集 ViewModel 的 StateFlows
    val email by viewModel.email.collectAsState()
    val captcha by viewModel.captcha.collectAsState()
    val password by viewModel.password.collectAsState()
    val mailCode by viewModel.mailCode.collectAsState()
    val captchaImageUrl by viewModel.captchaImageUrl.collectAsState()
    val sendEmailCodeStatus by viewModel.sendEmailCodeStatus.collectAsState()

    // 初始加载图形验证码
    LaunchedEffect(Unit) {
        viewModel.fetchCaptchaImage()
    }

    // 处理发送邮箱验证码的反馈
    LaunchedEffect(sendEmailCodeStatus) {
        when (sendEmailCodeStatus) {
            true -> {
                Log.d("RegisterRoute", "邮箱验证码发送成功")
                // 可以在这里导航到下一个注册步骤或者显示成功消息
            }
            false -> {
                Log.e("RegisterRoute", "邮箱验证码发送失败，请重试")
                // 刷新图形验证码，并提示用户
                viewModel.fetchCaptchaImage()
            }
            else -> { /* 初始状态或加载中 */ }
        }
    }

    RegisterScreen(
        email = email,
        onEmailChange = { viewModel.updateEmail(it) },
        captcha = captcha,
        onCaptchaChange = { viewModel.updateCaptcha(it) },
        captchaImageUrl = captchaImageUrl,
        onCaptchaImageClick = { viewModel.fetchCaptchaImage() }, // 点击图片刷新验证码
        onSendEmailCodeClick = { viewModel.requestEmailVerificationCode(email, captcha) }, // 传递 ViewModel 中的状态
        emailCodeSentStatus = sendEmailCodeStatus,
        password = password,
        onPasswordChange = { viewModel.updatePassword(it) },
        mailCode = mailCode,
        onMailCodeChange = { viewModel.updateMailCode(it) }, // 临时处理，需要更新 ViewModel
        onRegisterClick = { viewModel.register(email, mailCode, password) }
    )
}


@Composable
fun RegisterScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    captcha: String,
    onCaptchaChange: (String) -> Unit,
    captchaImageUrl: String?,
    onCaptchaImageClick: () -> Unit,
    onSendEmailCodeClick: () -> Unit,
    onRegisterClick:()->Unit,
    emailCodeSentStatus: Boolean?,
    password:String,
    onPasswordChange: (String) -> Unit,
    mailCode:String,
    onMailCodeChange: (String) -> Unit,
) {
    val context = LocalContext.current // 获取当前 Context

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "注册你的VicMusic账户",
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 邮箱输入框
        TextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("邮箱") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 图形验证码输入框和图片
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = captcha,
                onValueChange = onCaptchaChange,
                label = { Text("在此输入验证码") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(100.dp, 50.dp) // 调整验证码图片大小
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
                    .clickable(onClick = onCaptchaImageClick),
                contentAlignment = Alignment.Center
            ) {
                if (captchaImageUrl != null && captchaImageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context) // 使用 ImageRequest.Builder
                            .data(captchaImageUrl)
                            .diskCachePolicy(CachePolicy.DISABLED) // 禁用磁盘缓存
                            .memoryCachePolicy(CachePolicy.DISABLED) // 禁用内存缓存
                            .build(),
                        contentDescription = "图形",
                        modifier = Modifier.fillMaxSize(),
                        // Coil 默认会做图片缓存，需要注意如何处理验证码刷新
                    )
                } else {
                    Text("加载中...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        // 发送邮箱验证码按钮
        Button(
            onClick = onSendEmailCodeClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = emailCodeSentStatus != true // 可以在发送中禁用按钮
        ) {
            Text(
                text = when (emailCodeSentStatus) {
                    true -> "验证码已发送"
                    false -> "发送失败，请重试"
                    else -> "发送邮箱验证码"
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (emailCodeSentStatus == false) {
            Text("请检查邮箱和验证码", color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
        }
        if(emailCodeSentStatus==true){
            TextField(
                value = mailCode,
                onValueChange = onMailCodeChange,
                label = { Text("邮箱验证码") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("密码") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    "注册"
                )
            }
        }
    }
}