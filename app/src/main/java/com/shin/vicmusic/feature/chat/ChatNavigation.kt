package com.shin.vicmusic.feature.chat

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val CHAT_ROUTE = "chat/{userId}/{userName}"

fun NavController.navigateToChat(userId: String, userName: String) {
    this.navigate("chat/$userId/$userName")
}

fun NavGraphBuilder.chatScreen() {
    composable(
        route = CHAT_ROUTE,
        arguments = listOf(
            navArgument("userId") { type = NavType.StringType },
            navArgument("userName") { type = NavType.StringType }
        )
    ) {
        ChatRoute()
    }
}