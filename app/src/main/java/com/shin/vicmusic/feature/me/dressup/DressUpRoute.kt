package com.shin.vicmusic.feature.me.dressup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.theme.LocalAppColors
import com.shin.vicmusic.core.design.theme.AppBackground
import com.shin.vicmusic.core.design.theme.dressUp.dynamicGlow.DynamicGlowAppBackGround
import com.shin.vicmusic.core.manager.BackgroundStyle
import com.shin.vicmusic.feature.common.bar.CommonTopBar

@Composable
fun DressUpRoute(
    popBackStack: () -> Unit,
    viewModel: DressUpViewModel = hiltViewModel()
) {
    val currentStyle by viewModel.currentBackgroundStyle.collectAsState(initial = BackgroundStyle.DYNAMIC_GLOW)

    DressUpScreen(
        popBackStack = popBackStack,
        currentStyle = currentStyle,
        onStyleSelected = viewModel::updateBackgroundStyle
    )
}

@Composable
fun DressUpScreen(
    popBackStack: () -> Unit,
    currentStyle: BackgroundStyle,
    onStyleSelected: (BackgroundStyle) -> Unit
) {
    val appColors = LocalAppColors.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CommonTopBar(midText = "装扮", popBackStack = popBackStack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "选择你的专属背景效果",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = appColors.textColor
            )

            // DYNAMIC_GLOW Preview
            StylePreviewCard(
                title = "动态光晕",
                description = "跟随页面呼吸的灵动色彩，沉浸感十足",
                isSelected = currentStyle == BackgroundStyle.DYNAMIC_GLOW,
                onClick = { onStyleSelected(BackgroundStyle.DYNAMIC_GLOW) }
            ) {
                DynamicGlowAppBackGround(modifier = Modifier.fillMaxSize()) {}
            }

            // SOLID_COLOR Preview
            StylePreviewCard(
                title = "纯色渐变",
                description = "极简主义，安静柔和的视觉体验",
                isSelected = currentStyle == BackgroundStyle.SOLID_COLOR,
                onClick = { onStyleSelected(BackgroundStyle.SOLID_COLOR) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(appColors.gradientMid)
                )
            }

            // NONE Preview
            StylePreviewCard(
                title = "无背景",
                description = "极爽快的性能表现与系统默认配色",
                isSelected = currentStyle == BackgroundStyle.NONE,
                onClick = { onStyleSelected(BackgroundStyle.NONE) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
            }
        }
    }
}

@Composable
fun StylePreviewCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    previewContent: @Composable () -> Unit
) {
    val appColors = LocalAppColors.current
    val borderColor = if (isSelected) appColors.accentPrimary else appColors.glassBorder

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(appColors.glassWhite)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        // Preview Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, appColors.glassBorder, RoundedCornerShape(8.dp))
        ) {
            previewContent()
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(appColors.accentPrimary, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "使用中",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title & Description
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = appColors.textColor
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = appColors.textColor.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
