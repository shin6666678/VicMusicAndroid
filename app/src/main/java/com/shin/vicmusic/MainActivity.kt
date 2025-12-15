package com.shin.vicmusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import com.shin.vicmusic.core.design.composition.LocalPlayerManager
import com.shin.vicmusic.ui.MyApp
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.feature.player.PlayerManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var playerManager: PlayerManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            VicMusicTheme {
                CompositionLocalProvider(
                    LocalPlayerManager provides playerManager
                ) {
                    MyApp(navController=navController)
                }
            }
        }
    }
}