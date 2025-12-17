package com.shin.vicmusic.core.domain

data class VipProduct(
    val id: String,
    val name: String,
    val price: String,
    val originalPrice: String? = null,
    val discountTag: String? = null,
    val description: String? = null,
    val isRecommended: Boolean = false
)

data class VipPrivilege(
    val id: Int,
    val name: String,
    val iconRes: Int // 这里实际项目中可以用Drawable资源ID，这里暂时用Int占位
)