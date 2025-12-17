package com.shin.vicmusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import com.shin.vicmusic.core.design.composition.LocalAuthManager
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.design.composition.LocalPlaybackQueueManager
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.feature.auth.AuthManager
import com.shin.vicmusic.feature.playList.PlaybackQueueManager
import com.shin.vicmusic.feature.player.PlayerManager
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 建立 Manager 之间的依赖关联
        playerManager.setQueueManager(playbackQueueManager)
        playbackQueueManager.setPlayerManager(playerManager)

        setContent {
            val navController = rememberNavController()
            VicMusicTheme {
                CompositionLocalProvider(
                    LocalPlayerManager provides playerManager,
                    LocalNavController provides navController,
                    LocalPlayerManager provides playerManager,
                    LocalPlaybackQueueManager provides playbackQueueManager,
                    LocalAuthManager provides authManager
                ) {
                    MyApp(navController = navController)
                }
            }
        }
    }
}