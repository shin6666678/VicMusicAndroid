package com.shin.vicmusic.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shin.vicmusic.R
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.util.SuperDateUtil

@Composable
fun SplashRoute(
    toMain:()->Unit,
    toLogin:()->Unit,
    viewModel: SplashViewModel= viewModel(),
){
    val timeLeft by viewModel.timeLeft.collectAsStateWithLifecycle()
    val navigateToMain by viewModel.navigateToMain.collectAsState()

    SplashScreen(
        year = SuperDateUtil.currentYear(),
        timeLeft=timeLeft,
        onSkipAdClick=viewModel::onSkipAdClick
    )
    // 当倒计时结束 (navigateToMain 变为 true) 且登录状态明确时才进行导航
    LaunchedEffect(navigateToMain, ) {
        if(navigateToMain) {
            toMain()
//            when (isLoggedIn) {
//                true -> {
//                    Log.d("SplashRoute", "倒计时结束，用户已登录。跳转到主页。")
//                    toMain()
//                }
//                false -> {
//                    Log.d("SplashRoute", "倒计时结束，用户未登录。跳转到登录页。")
//                    toLogin()
//                }
//                null -> {
//                    // 倒计时已结束，但登录状态仍在确定中。
//                    // 由于LaunchedEffect监听了isLoggedIn，当它变为true/false时，会再次触发。
//                    Log.d("SplashRoute", "倒计时结束，登录状态仍在检查中，等待...")
//                }
//            }
        }
    }
}
@Composable
fun SplashScreen(
    year: Int = 2024,
    onSkipAdClick: () -> Unit = {},
    timeLeft: Long=0
){
    Box(modifier = Modifier.fillMaxSize().background(color = Color.White)){
        Image(
            painter = painterResource(id=R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.padding(top = 120.dp).align(Alignment.TopCenter).size(200.dp)
        )
        Text(
            text = "维克音乐 , 一起聆听世界",
            color = Color.Black,
            modifier = Modifier
                .padding(bottom = 70.dp)
                .align(Alignment.BottomCenter)
        )
        Text(
            text = stringResource(id = R.string.copyright,year),
            color = Color.Black,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(30.dp).align(Alignment.BottomCenter)
        )
        Text(
            text = "倒计时,$timeLeft",
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(top = 100.dp, end = 100.dp)
                .clickable{
                    onSkipAdClick()
                }
        )
    }
}
@Composable
@Preview(showBackground = true)
fun SplashRoutePreView(): Unit {
    VicMusicTheme {
        SplashScreen()
    }
}