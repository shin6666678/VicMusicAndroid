package com.shin.vicmusic.core.model.response

import kotlinx.serialization.Serializable

/**
 * 解析网络响应
 */
@Serializable
data class NetworkResponse<T>(
    val status:Int=0,
    val message:String?=null,
    val data: T? =null
)