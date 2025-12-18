package com.shin.vicmusic.feature.artist.artistDetail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.shin.vicmusic.feature.artist.artistList.ArtistListRoute

const val ARTIST_DETAIL_ROUTE = "artist_detail"
fun NavGraphBuilder.artistDetailScreen() {
    composable(ARTIST_DETAIL_ROUTE) {
        ArtistDetailRoute()
    }
}

fun NavController.navigateToArtistDetail() {
    this.navigate(ARTIST_DETAIL_ROUTE)
}