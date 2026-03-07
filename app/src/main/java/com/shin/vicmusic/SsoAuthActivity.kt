package com.shin.vicmusic

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.shin.vicmusic.core.config.AppGlobalData
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.feature.auth.LoginScreen

class SsoAuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 检查当前 Compose 应用是否本身已经登录
        if (!AppGlobalData.token.isNullOrEmpty()) {
            // 已登录，直接将 Token(令牌) 打包返回给拉起它的 View 应用
            val resultIntent = Intent().apply {
                putExtra("auth_token", AppGlobalData.token)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        } else {
            // 未登录，展示纯 Compose 编写的登录 UI，引导用户登录
            setContent {
                VicMusicTheme {
                    // 复用您项目中现有的 LoginScreen(登录屏幕) Composable
                    LoginScreen(
                        uiState = TODO(),
                        onIntent = TODO(),
                        onRegisterClick = TODO()
                    )
                }
            }
        }
    }
}