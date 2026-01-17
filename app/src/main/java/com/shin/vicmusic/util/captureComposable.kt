package com.shin.vicmusic.util

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.drawToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * [终极修复·手动指定尺寸版] 将一个 Composable 渲染成 Bitmap。
 * 此版本不再依赖Compose的自我测量，而是手动将dp转换为px，并强制应用尺寸。
 * 这是解决顽固“尺寸为0”问题的最终手段。
 * 这个函数必须在主线程上调用。
 */
suspend fun captureComposable(
    context: Context,
    parentComposition: CompositionContext,
    content: @Composable () -> Unit
): Bitmap = withContext(Dispatchers.Main) { // 确保在主线程执行

    val composeView = ComposeView(context).apply {
        setParentCompositionContext(parentComposition)
        setContent(content)
    }

    // 创建一个虚拟的父容器
    val dummyParent = object : ViewGroup(context) {
        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) { /* no-op */ }
    }
    dummyParent.addView(composeView)

    // ==================== 终极核心修复 ====================

    // 1. 定义我们想要的DP尺寸 (与SongShareCard中的尺寸保持一致)
    val widthInDp = 300
    val heightInDp = 450

    // 2. 将DP转换为像素(PX)
    val density = context.resources.displayMetrics.density
    val widthInPx = (widthInDp * density).toInt()
    val heightInPx = (heightInDp * density).toInt()

    // 3. 使用 MeasureSpec.EXACTLY 创建一个精确的测量规格
    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(widthInPx, View.MeasureSpec.EXACTLY)
    val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(heightInPx, View.MeasureSpec.EXACTLY)

    // 4. 使用精确的规格来测量和布局
    composeView.measure(widthMeasureSpec, heightMeasureSpec)
    composeView.layout(0, 0, widthInPx, heightInPx)

    // =======================================================

    // 因为我们手动指定了尺寸，所以这里的检查现在只是一个保险
    if (composeView.width <= 0 || composeView.height <= 0) {
        throw IllegalStateException("手动指定尺寸后，截图尺寸依然为0，发生未知错误。")
    }

    // 使用 drawToBitmap 进行截图
    val bitmap = composeView.drawToBitmap(Bitmap.Config.ARGB_8888)

    return@withContext bitmap
}
