package com.shin.vicmusic.feature.artist.artistList

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
const val ARTIST_LIST_ROUTE = "artist_list"
fun NavGraphBuilder.artistListScreen() {
    composable(ARTIST_LIST_ROUTE) {
        ArtistListRoute()
    }
}

fun NavController.navigateToArtistList() {
    this.navigate(ARTIST_LIST_ROUTE)
}