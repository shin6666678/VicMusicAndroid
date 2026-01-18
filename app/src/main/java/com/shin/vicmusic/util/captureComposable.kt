package com.shin.vicmusic.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * 将 Composable 渲染为 Bitmap。
 * 修复了离屏渲染不触发 Compose 绘制流程的问题。
 */
suspend fun captureComposable(
    context: Context,
    parentComposition: CompositionContext,
    content: @Composable () -> Unit
): Bitmap = withContext(Dispatchers.Main) {

    val activity = context.findActivity() ?: throw IllegalStateException("无法从 Context 中获取 Activity")
    val rootView = activity.window.decorView as ViewGroup

    // 1. 创建并配置 ComposeView
    val composeView = ComposeView(context).apply {
        setParentCompositionContext(parentComposition)
        setContent(content)

        // 绑定必要的 Owner，确保逻辑可运行
        setViewTreeLifecycleOwner(activity as? LifecycleOwner)
        setViewTreeViewModelStoreOwner(activity as? ViewModelStoreOwner)
        setViewTreeSavedStateRegistryOwner(activity as? SavedStateRegistryOwner)

        // 设置为不可见，但不使用 GONE，因为 GONE 不会触发绘制
        visibility = View.INVISIBLE
    }

    // 2. 将其添加到真实的视图层级中（重要：解决渲染引擎不启动的问题）
    // 临时将其添加到根布局，位置设在屏幕外
    val params = FrameLayout.LayoutParams(
        (300 * context.resources.displayMetrics.density).toInt(),
        (450 * context.resources.displayMetrics.density).toInt()
    )
    rootView.addView(composeView, params)

    // 3. 强制测量和布局
    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(params.width, View.MeasureSpec.EXACTLY)
    val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(params.height, View.MeasureSpec.EXACTLY)
    composeView.measure(widthMeasureSpec, heightMeasureSpec)
    composeView.layout(0, 0, params.width, params.height)

    // 4. 等待 Compose 渲染
    // 附着后需要给 Compose 引擎时间来处理 setContent 和初始绘制指令
    delay(500)

    // 5. 创建 Bitmap 并执行绘制
    val bitmap = Bitmap.createBitmap(params.width, params.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // 强制刷新绘制
    canvas.drawColor(AndroidColor.WHITE) // 底色
    composeView.draw(canvas)

    // 6. 清理现场
    rootView.removeView(composeView)

    return@withContext bitmap
}

/**
 * 递归寻找 Activity，处理 Hilt 或其他 ContextWrapper 包装的情况
 */
private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}