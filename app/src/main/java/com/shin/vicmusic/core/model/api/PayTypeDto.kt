package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class PayTypeDto(
    val value: Int,
    val name: String
)