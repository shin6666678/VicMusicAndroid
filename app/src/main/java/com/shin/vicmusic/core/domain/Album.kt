package com.shin.vicmusic.core.domain // 建议将 core.model 迁移至 core.domain

// ... 其他 import

/**
 * 专辑领域模型 (Domain Model)
 */
data class Album (
    val id:String,
    val title:String,
    // ... 简化字段，避免 songs 的循环依赖
    val icon:String?=null,
    val detail:String?=null,
    val clicksCount:Long=0,
    val collectsCount:Long=0,
    val commentsCount:Long=0,
    val songsCount:Long=0,
    // 移除 comments, created, updated, user 等，只保留核心字段
)