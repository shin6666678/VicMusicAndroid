package com.shin.vicmusic.feature.discovery.recommend

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.shin.vicmusic.feature.artist.artistList.ArtistListRoute

const val RECOMMEND_ROUTE = "recommend"
fun NavGraphBuilder.recommendScreen() {
    composable(RECOMMEND_ROUTE) {
        ArtistListRoute()
    }
}

fun NavController.navigateToRecommend() {
    this.navigate(RECOMMEND_ROUTE)
}