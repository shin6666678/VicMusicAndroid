package com.shin.vicmusic.feature.relationship

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val RELATIONSHIP_ROUTE = "relationship"

fun NavGraphBuilder.relationshipScreen() {
    composable(
        route = "$RELATIONSHIP_ROUTE/{tabIndex}",
        arguments = listOf(
            navArgument("tabIndex") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        // 获取参数并转回枚举 (默认为 0/FOLLOWING)
        val index = backStackEntry.arguments?.getInt("tabIndex") ?: 0
        val tab = RelationshipTab.entries.getOrElse(index) { RelationshipTab.FOLLOWING }

        RelationShipRoute(initialTab = tab)
    }
}

fun NavController.navigateToRelationship(tab: RelationshipTab) {
    this.navigate("$RELATIONSHIP_ROUTE/${tab.ordinal}")
}