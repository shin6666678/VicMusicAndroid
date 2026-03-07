package com.shin.vicmusic.feature.me.audio

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val AUDIO_ROUTE = "audio_route"

fun NavController.navigateToAudio(navOptions: NavOptions? = null) {
    this.navigate(AUDIO_ROUTE, navOptions)
}

fun NavGraphBuilder.audioScreen(
    onBackClick: () -> Unit
) {
    composable(route = AUDIO_ROUTE) {
        AudioRoute(
            onBackClick = onBackClick
        )
    }
}
