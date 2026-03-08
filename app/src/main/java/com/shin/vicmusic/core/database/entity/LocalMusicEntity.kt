package com.shin.vicmusic.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_music")
data class LocalMusicEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val artistName: String,
    val uri: String,
    val albumName: String? = null,
    val duration: Long = 0,
    val icon: String? = null
)
