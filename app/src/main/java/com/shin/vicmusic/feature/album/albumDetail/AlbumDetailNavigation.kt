package com.shin.vicmusic.feature.album.albumDetail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val ALBUM_DETAIL_ROUTE = "album_detail"
const val ALBUM_ID_ARG = "albumId"

fun NavController.navigateToAlbumDetail(albumId: String) {
    this.navigate("$ALBUM_DETAIL_ROUTE/$albumId")
}

fun NavGraphBuilder.albumDetailScreen() {
    composable(
        route = "$ALBUM_DETAIL_ROUTE/{$ALBUM_ID_ARG}",
        arguments = listOf(navArgument(ALBUM_ID_ARG) { type = NavType.StringType })
    ) {
        AlbumDetailRoute()
    }
}