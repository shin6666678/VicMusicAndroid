package com.shin.vicmusic.feature.comment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val COMMENT_ROUTE = "commentDetail"

// 2. 创建 NavGraphBuilder 的扩展函数，用于在 AppNavHost 中构建路由
fun NavGraphBuilder.commentScreen() {
    composable(
        // 路由路径现在包含了必需的参数
        route = "$COMMENT_ROUTE/{resourceId}/{resourceType}",
        arguments = listOf(
            navArgument("resourceId") { type = NavType.StringType },
            navArgument("resourceType") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        // 从 backStackEntry 中安全地获取参数
        val resourceId = backStackEntry.arguments?.getString("resourceId") ?: ""
        val resourceType = backStackEntry.arguments?.getString("resourceType") ?: ""

        // 调用该路由对应的 Composable
        CommentRoute(resourceId = resourceId, resourceType = resourceType)
    }
}

// 3. 创建 NavController 的扩展函数，用于类型安全地导航
fun NavController.navigateToComment(resourceId: String, resourceType: String) {
    // 调用此函数即可导航，无需在外部拼接字符串
    this.navigate("$COMMENT_ROUTE/$resourceId/$resourceType")
}
