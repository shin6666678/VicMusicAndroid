package com.shin.vicmusic.core.model.api

import com.shin.vicmusic.core.domain.ActivityType
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.domain.User
import kotlinx.serialization.Serializable

@Serializable
data class FeedItemDto(
    val id: String,
    val timestamp: Long,
    val user: User,
    val comment: String? = null,
    val targetType: String? = null,
    val activityType: ActivityType? = null,
    val content: FeedContentDto? = null
)

@Serializable
data class FeedContentDto(
    val type: String,
    val song: Song? = null,
    val playlist: Playlist? = null,
    val album: Album? = null
)
