package com.shin.vicmusic.feature.album.albumList

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ALBUM_LIST_ROUTE = "album_list"

fun NavController.navigateToAlbumList() {
    this.navigate(ALBUM_LIST_ROUTE)
}

fun NavGraphBuilder.albumListScreen(
) {
    composable(ALBUM_LIST_ROUTE) {
        AlbumListRoute()
    }
}