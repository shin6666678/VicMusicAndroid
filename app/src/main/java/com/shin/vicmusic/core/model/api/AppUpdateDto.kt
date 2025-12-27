package com.shin.vicmusic.core.model.api

import kotlinx.serialization.Serializable

@Serializable
data class AppUpdateDto(
    val hasUpdate: Boolean = false,
    val isForce: Boolean = false,
    val version: String? = null,
    val content: String? = null,
    val downloadUrl: String? = null
)