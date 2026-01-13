package com.shin.vicmusic.feature.localMusic

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val LOCAL_MUSIC_ROUTE = "local_music"
fun NavGraphBuilder.localMusicScreen() {
    composable(route = LOCAL_MUSIC_ROUTE) {
        LocalMusicScreen()
    }
}

fun NavController.navigateToLocalMusic(navOptions: NavOptions? = null) {
    this.navigate(LOCAL_MUSIC_ROUTE, navOptions)
}
