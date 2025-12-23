package com.shin.vicmusic.core.model.response

import kotlinx.serialization.Serializable

/**
 * 分页模型
 */
@Serializable
data class NetworkPageMeta(
    val total:Int=-1,
    val pages:Int=-1,
    val size:Int=-1,
    val page:Int=-1,
    val next:Int=-1
)
