package com.shin.vicmusic.feature.artist.artistDetail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val ARTIST_DETAIL_ROUTE = "artist_detail"
fun NavGraphBuilder.artistDetailScreen() {
    composable(
        route = "$ARTIST_DETAIL_ROUTE/{artistId}",
        arguments = listOf(
            navArgument("artistId") { type = NavType.StringType }
        )
    ) {
        ArtistDetailRoute()
    }
}

fun NavController.navigateToArtistDetail(artistId: String) {
    this.navigate("$ARTIST_DETAIL_ROUTE/$artistId")
}