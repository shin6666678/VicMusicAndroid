package com.shin.vicmusic.feature.vip.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shin.vicmusic.core.domain.VipProduct
import com.shin.vicmusic.feature.vip.VipCardBg
import com.shin.vicmusic.feature.vip.VipGold
import com.shin.vicmusic.feature.vip.VipLightText
import com.shin.vicmusic.feature.vip.VipSubText

@Composable
fun VipProductList(
    products: List<VipProduct>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(products) { index, product ->
            VipProductItem(
                product = product,
                isSelected = index == selectedIndex,
                onClick = { onSelect(index) }
            )
        }
    }
}

@Composable
fun VipProductItem(
    product: VipProduct,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) VipGold else Color.Transparent
    val bgColor = if (isSelected) VipGold.copy(alpha = 0.15f) else VipCardBg

    Box(
        modifier = Modifier
            .width(110.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(if (isSelected) 2.dp else 0.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = product.name,
                color = VipLightText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = product.price,
                    color = VipGold,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                product.originalPrice?.let {
                    Text(
                        text = it,
                        color = VipSubText,
                        fontSize = 12.sp,
                        style = androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.LineThrough)
                    )
                }
            }

            product.description?.let {
                Text(
                    text = it,
                    color = if (isSelected) VipGold else VipSubText,
                    fontSize = 11.sp
                )
            }
        }

        // 推荐标签
        if (product.discountTag != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFFE53935), Color(0xFFEF5350))
                        ),
                        shape = RoundedCornerShape(bottomEnd = 8.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = product.discountTag,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}