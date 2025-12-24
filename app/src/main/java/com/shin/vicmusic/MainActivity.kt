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
}