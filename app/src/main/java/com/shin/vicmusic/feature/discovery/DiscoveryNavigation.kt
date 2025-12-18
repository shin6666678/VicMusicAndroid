package com.shin.vicmusic.feature.discovery

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.shin.vicmusic.feature.artist.artistList.ArtistListRoute

const val DISCOVERY_ROUTE = "discovery"


fun NavGraphBuilder.discoveryScreen(navController: NavController) {
    composable(DISCOVERY_ROUTE) {
        DiscoveryRoute(navController = navController)
    }
}


