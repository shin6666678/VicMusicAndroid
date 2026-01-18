package com.shin.vicmusic

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import com.shin.vicmusic.core.design.composition.LocalAuthManager
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.design.composition.LocalPlaybackQueueManager
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.design.composition.LocalSongActionManager
import com.shin.vicmusic.core.design.composition.LocalTokenManager
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.core.manager.AuthManager
import com.shin.vicmusic.core.manager.PlaybackQueueManager
import com.shin.vicmusic.core.manager.PlayerManager
import com.shin.vicmusic.core.manager.SongActionManager
import com.shin.vicmusic.core.manager.TokenManager
import com.shin.vicmusic.ui.MyApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            VicMusicTheme {
                CompositionLocalProvider(
                    LocalPlayerManager provides playerManager,
                    LocalNavController provides navController,
                    LocalPlaybackQueueManager provides playbackQueueManager,
                    LocalAuthManager provides authManager,
                    LocalTokenManager provides tokenManager,
                    LocalSongActionManager provides songActionManager
                ) {
                    MyApp()
                }
            }
        }
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