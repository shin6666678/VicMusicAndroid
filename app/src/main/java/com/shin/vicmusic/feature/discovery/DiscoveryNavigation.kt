package com.shin.vicmusic.feature.discovery

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.shin.vicmusic.feature.discovery.musicHall.ArtistListScreen

const val DISCOVERY_ROUTE = "discovery"
const val ARTIST_LIST_ROUTE = "artist_list"

fun NavGraphBuilder.discoveryScreen(navController: NavController) {
    composable(DISCOVERY_ROUTE) {
        DiscoveryRoute(navController = navController)
    }
}

fun NavGraphBuilder.artistListScreen() {
    composable(ARTIST_LIST_ROUTE) {
        ArtistListScreen()
    }
}

fun NavController.navigateToArtistList() {
    this.navigate(ARTIST_LIST_ROUTE)
}
