package com.shin.vicmusic.core.domain

import kotlinx.serialization.Serializable

// 建议将 core.model 迁移至 core.domain

// ... 其他 import

/**
 * 专辑领域模型 (Domain Model)
 */
@Serializable
data class Album (
    val id:String,
    val title:String,
    val artist: Artist?=null,
    val icon:String?=null,
    val description:String?=null,
    val company:String?=null,
    val releaseTime:String?=null,
    val style:String?=null,
    val songCount:Int=0,
)