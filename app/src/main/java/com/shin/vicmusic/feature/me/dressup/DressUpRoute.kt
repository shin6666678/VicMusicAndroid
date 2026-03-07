package com.shin.vicmusic.feature.me.dressup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.shin.vicmusic.core.manager.DressUpStyle
import com.shin.vicmusic.feature.common.bar.CommonTopBar

@Composable
fun DressUpRoute(
    popBackStack: () -> Unit,
    viewModel: DressUpViewModel = hiltViewModel()
) {
    val currentStyle by viewModel.currentDressUpStyle.collectAsState(initial = DressUpStyle.SYSTEM_DEFAULT)

    DressUpScreen(
        popBackStack = popBackStack,
        currentStyle = currentStyle,
        onStyleSelected = viewModel::updateDressUpStyle
    )
}

@Composable
fun DressUpScreen(
    popBackStack: () -> Unit,
    currentStyle: DressUpStyle,
    onStyleSelected: (DressUpStyle) -> Unit
) {
    val appColors = LocalAppColors.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CommonTopBar(midText = "装扮中心", popBackStack = popBackStack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "选择你的专属主题与背景",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = appColors.textColor
            )

            // SYSTEM_DEFAULT
            StylePreviewCard(
                title = "系统默认",
                description = "跟随系统亮暗设置，保持原生体验",
                isSelected = currentStyle == DressUpStyle.SYSTEM_DEFAULT,
                onClick = { onStyleSelected(DressUpStyle.SYSTEM_DEFAULT) }
            ) {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
            }

            // GLOW Styles
            SectionTitle("动态光晕")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    StylePreviewCard(
                        title = "浅色光晕",
                        isSelected = currentStyle == DressUpStyle.LIGHT_GLOW,
                        onClick = { onStyleSelected(DressUpStyle.LIGHT_GLOW) },
                        height = 100.dp
                    ) {
                        DynamicGlowAppBackGround(modifier = Modifier.fillMaxSize()) {}
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    StylePreviewCard(
                        title = "深色光晕",
                        isSelected = currentStyle == DressUpStyle.DARK_GLOW,
                        onClick = { onStyleSelected(DressUpStyle.DARK_GLOW) },
                        height = 100.dp
                    ) {
                        DynamicGlowAppBackGround(modifier = Modifier.fillMaxSize()) {}
                    }
                }
            }

            // SOLID Styles
            SectionTitle("极简纯色")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    StylePreviewCard(
                        title = "清爽白",
                        isSelected = currentStyle == DressUpStyle.LIGHT_SOLID,
                        onClick = { onStyleSelected(DressUpStyle.LIGHT_SOLID) },
                        height = 100.dp
                    ) {
                        Box(Modifier.fillMaxSize().background(Color(0xFFF1F5F9)))
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    StylePreviewCard(
                        title = "深邃蓝",
                        isSelected = currentStyle == DressUpStyle.DARK_SOLID,
                        onClick = { onStyleSelected(DressUpStyle.DARK_SOLID) },
                        height = 100.dp
                    ) {
                        Box(Modifier.fillMaxSize().background(Color(0xFF0F172A)))
                    }
                }
            }

            // NONE Styles
            SectionTitle("无背景")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    StylePreviewCard(
                        title = "纯净浅色",
                        isSelected = currentStyle == DressUpStyle.LIGHT_NONE,
                        onClick = { onStyleSelected(DressUpStyle.LIGHT_NONE) },
                        height = 100.dp
                    ) {
                        Box(Modifier.fillMaxSize().background(Color.White))
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    StylePreviewCard(
                        title = "纯净深色",
                        isSelected = currentStyle == DressUpStyle.DARK_NONE,
                        onClick = { onStyleSelected(DressUpStyle.DARK_NONE) },
                        height = 100.dp
                    ) {
                        Box(Modifier.fillMaxSize().background(Color(0xFF020617)))
                    }
                }
            }

            // SPECIAL Themes
            SectionTitle("特别主题")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    StylePreviewCard(
                        title = "RED",
                        description = "纯白背景与鲜艳红色文字",
                        isSelected = currentStyle == DressUpStyle.RED,
                        onClick = { onStyleSelected(DressUpStyle.RED) },
                        height = 100.dp
                    ) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.White)
                        ) {
                            Text(
                                "Aa",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = LocalAppColors.current.textColor.copy(alpha = 0.6f),
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun StylePreviewCard(
    title: String,
    description: String? = null,
    isSelected: Boolean,
    height: androidx.compose.ui.unit.Dp = 120.dp,
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
                .height(height)
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
                        text = "已应用",
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
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = appColors.textColor.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
