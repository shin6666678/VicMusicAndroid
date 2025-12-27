package com.shin.vicmusic.feature.playlist.publicList

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val PUBLIC_PLAYLISTS_ROUTE = "public_playlists"

fun NavGraphBuilder.publicPlaylistScreen(
) {
    composable(route = PUBLIC_PLAYLISTS_ROUTE) {
        PublicPlaylistRoute()
    }
}

fun NavController.navigateToPublicPlaylists() {
    this.navigate(PUBLIC_PLAYLISTS_ROUTE)
}