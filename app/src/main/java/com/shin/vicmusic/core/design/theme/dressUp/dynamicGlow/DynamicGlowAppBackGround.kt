package com.shin.vicmusic.core.design.theme.dressUp.dynamicGlow

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.theme.LocalAppColors
@Composable
fun DynamicGlowAppBackGround(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val appColors = LocalAppColors.current
    val accentPrimary = appColors.accentPrimary
    val accentSecondary = appColors.accentSecondary

    // ---- 浮动光晕动画 ----
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glow1X by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow1x"
    )
    val glow2X by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow2x"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        appColors.gradientStart,
                        appColors.gradientMid,
                        appColors.gradientEnd
                    )
                )
            )
    ) {
        // ---- 背景光晕球 ----
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (glow1X * 200 - 100).dp, y = (-50).dp)
                .blur(80.dp)
                .background(
                    Brush.radialGradient(
                        listOf(accentPrimary.copy(alpha = 0.35f), Color.Transparent),
                        center = Offset.Zero, radius = 600f
                    ),
                    shape = RoundedCornerShape(50)
                )
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (glow2X * 80).dp, y = (-(glow2X * 60)).dp)
                .blur(70.dp)
                .background(
                    Brush.radialGradient(
                        listOf(accentSecondary.copy(alpha = 0.3f), Color.Transparent),
                        center = Offset.Zero, radius = 500f
                    ),
                    shape = RoundedCornerShape(50)
                )
        )

        content()
    }
}