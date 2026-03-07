package com.shin.vicmusic

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.rememberNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme
import com.shin.vicmusic.core.design.composition.LocalAuthManager
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.design.composition.LocalPlaybackQueueManager
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.design.composition.LocalSongActionManager
import com.shin.vicmusic.core.design.composition.LocalTokenManager
import com.shin.vicmusic.core.design.theme.LocalAppColors
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.core.design.theme.darkAppColors
import com.shin.vicmusic.core.design.theme.lightAppColors
import com.shin.vicmusic.core.design.theme.redAppColors
import com.shin.vicmusic.core.manager.AuthManager
import com.shin.vicmusic.core.manager.DressUpStyle
import com.shin.vicmusic.core.manager.PlaybackQueueManager
import com.shin.vicmusic.core.manager.PlayerManager
import com.shin.vicmusic.core.manager.SongActionManager
import com.shin.vicmusic.core.manager.ThemeManager
import com.shin.vicmusic.core.manager.TokenManager
import com.shin.vicmusic.core.worker.MessageCheckWorker
import com.shin.vicmusic.feature.main.MainViewModel
import com.shin.vicmusic.ui.MyApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var playerManager: PlayerManager
    @Inject
    lateinit var playbackQueueManager: PlaybackQueueManager
    @Inject
    lateinit var authManager: AuthManager
    @Inject
    lateinit var tokenManager: TokenManager
    @Inject
    lateinit var songActionManager: SongActionManager
    @Inject
    lateinit var themeManager: ThemeManager

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // 初始化后台消息检查任务（WorkManager）
        scheduleMessageCheckWorker()

        splashScreen.setKeepOnScreenCondition {
            !mainViewModel.isReady.value
        }

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val dressUpStyle by themeManager.dressUpStyle.collectAsState(initial = DressUpStyle.SYSTEM_DEFAULT)
            val isSystemDark = isSystemInDarkTheme()

            val currentAppColors = when (dressUpStyle) {
                DressUpStyle.SYSTEM_DEFAULT -> if (isSystemDark) darkAppColors else lightAppColors
                DressUpStyle.LIGHT_GLOW -> lightAppColors
                DressUpStyle.DARK_GLOW -> darkAppColors
                DressUpStyle.LIGHT_SOLID -> lightAppColors
                DressUpStyle.DARK_SOLID -> darkAppColors
                DressUpStyle.LIGHT_NONE -> lightAppColors
                DressUpStyle.DARK_NONE -> darkAppColors
                DressUpStyle.RED -> redAppColors
            }
            
            CompositionLocalProvider(
                LocalPlayerManager provides playerManager,
                LocalNavController provides navController,
                LocalPlaybackQueueManager provides playbackQueueManager,
                LocalAuthManager provides authManager,
                LocalTokenManager provides tokenManager,
                LocalSongActionManager provides songActionManager,
                LocalAppColors provides currentAppColors
            ) {
                VicMusicTheme(appColors = currentAppColors) {
                    MyApp()
                }
            }
        }
    }

    /**
     * 设置 WorkManager 定期任务，用于后台私信检查。
     */
    private fun scheduleMessageCheckWorker() {
        // 设置限制条件：必须有网络连接
        val constraints = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
            .build()

        // 创建周期性任务请求：每隔 15 分钟运行一次 (WorkManager 最小间隔)
        val workRequest = PeriodicWorkRequestBuilder<MessageCheckWorker>(
            15, java.util.concurrent.TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        // 提交任务到 WorkManager
        // 使用 KEEP 策略，确保任务不会被重复提交
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "MessageCheckWork",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    /**
     * ‼️ 只需要保留这个：当 App 在后台运行，点击 DeepLink 唤起时，
     * 必须更新 Intent，否则 NavHost 可能拿不到最新的跳转参数。
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}