package com.shin.vicmusic.core.model.response

import kotlinx.serialization.Serializable

/**
 * 分页模型
 */
@Serializable
data class NetworkPageMeta(
    val total:Int?=null,
    val pages:Int?=null,
    val size:Int?=null,
    val page:Int?=null,
    val next:Int?=null
)
