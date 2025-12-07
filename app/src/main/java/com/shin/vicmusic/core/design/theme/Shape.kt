package com.shin.vicmusic.core.design.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val extraSmallRoundedCornerShape= RoundedCornerShape(5.dp)

val myShapes = Shapes(
    extraSmall = extraSmallRoundedCornerShape,
    small = RoundedCornerShape(5.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(30.dp)
)