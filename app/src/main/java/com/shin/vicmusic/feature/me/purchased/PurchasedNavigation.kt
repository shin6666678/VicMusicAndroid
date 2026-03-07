package com.shin.vicmusic.feature.me.purchased

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val PURCHASED_ROUTE = "purchased_route"

fun NavController.navigateToPurchased(navOptions: NavOptions? = null) {
    this.navigate(PURCHASED_ROUTE, navOptions)
}

fun NavGraphBuilder.purchasedScreen(
    onBackClick: () -> Unit
) {
    composable(route = PURCHASED_ROUTE) {
        PurchasedRoute(
            onBackClick = onBackClick
        )
    }
}
