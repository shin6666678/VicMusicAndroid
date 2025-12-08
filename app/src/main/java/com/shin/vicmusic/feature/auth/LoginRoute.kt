package com.shin.vicmusic.feature.auth

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController // 确保导入 NavController
import com.shin.vicmusic.R
import com.shin.vicmusic.feature.auth.LoginViewModel

@Composable
@Preview
fun PreView(){
    LoginScreen(801,null,null)
}

@Composable
fun LoginRoute(navController: NavController, viewModel: LoginViewModel = viewModel()) { // 添加 navController 参数

    val qrcodeAuthStatus by viewModel.qrcodeAuthStatus.collectAsState()
    val qrcodeBitmap by viewModel.qrcodeBitmap.collectAsState()
    val getAccountInfoSuccess by viewModel.getAccountInfoSuccess.collectAsState()
    LoginScreen(qrcodeAuthStatus, qrcodeBitmap, getAccountInfoSuccess) // 修复：传递实际的 qrcodeAuthStatus

    LaunchedEffect(Unit) {
        viewModel.qrcodeAuth()
    }
    LaunchedEffect(qrcodeAuthStatus) {
        if (qrcodeAuthStatus == 800) {  // 二维码过期，重走认证流程
            Log.e("ssk", "----二维码过期，重新生成")
            viewModel.qrcodeAuth()
        }
    }
    LaunchedEffect(getAccountInfoSuccess) {
        if (getAccountInfoSuccess == true) {
            navController.popBackStack() // 使用传入的 navController
            navController.navigate("home_route") // 替换为你的主页路由，这里使用"home_route"作为示例
        } else if (getAccountInfoSuccess == false) {  // 获取用户信息失败，重走认证流程
            viewModel.qrcodeAuth()
        }
    }
}


@Composable
fun LoginScreen(
    qrcodeAuthStatus: Int?=801,
    qrcodeBitmap: Bitmap?,
    getAccountInfoSuccess: Boolean?
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
            //.background(AppColorsProvider.current.primary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Box(
                Modifier
                    .padding(top = 205.dp, start = 5.dp)
                    .size(190.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_splash_logo),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 200.dp)
                    .size(200.dp)
                    .clip(RoundedCornerShape(50)),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 10.dp)
                .width(250.dp)
                .background(MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "扫码登录体验",
                fontSize = 40.sp,
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
            )
            if (qrcodeAuthStatus == 801 || qrcodeAuthStatus == 802) {
                Image(
                    //bitmap = qrcodeBitmap!!.asImageBitmap(),
                    painter = painterResource(R.drawable.ic_launcher),
                    modifier = Modifier.size(40.dp),
                    contentDescription = "登录二维码"
                )
            } else {
                Box(modifier = Modifier.size(400.dp), contentAlignment = Alignment.Center) {
                    //LoadingComponent(loadingWidth = 90.dp, loadingHeight = 75.dp)
                }
            }

            val tip = when (qrcodeAuthStatus) {
                801, 802 -> "请使用网易云音乐app扫码授权登录"
                803 -> "正在获取用户信息..."
                else -> "正在加载二维码"
            }

            Text(
                text = tip,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp)
            )
            Text(
                text = "(仅供学习使用)",
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 32.dp, start = 32.dp, end = 32.dp)
            )
        }
    }
}