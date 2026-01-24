package com.shin.vicmusic.core.domain

import kotlinx.serialization.Serializable

/**
 * Represents a single item in the user's feed.
 */
@Serializable
sealed class Feed {
    abstract val id: String
    abstract val timestamp: Long
}

/**
 * A user shares a song or a playlist with an optional comment, or posts a text-only update.
 */
@Serializable
data class UserPost(
    override val id: String,
    override val timestamp: Long,
    val user: User,
    val content: ShareableContent?,
    val comment: String?
) : Feed()

/**
 * A log of a user's activity, e.g., liking a song or creating a playlist.
 */
@Serializable
data class UserActivity(
    override val id: String,
    override val timestamp: Long,
    val user: User,
    val activityType: ActivityType,
    val content: ShareableContent
) : Feed()

enum class ActivityType(val description: String) {
    LIKED_SONG("喜欢了这首歌"),
    LIKED_PLAYLIST("收藏了这张歌单"),
    CREATED_PLAYLIST("创建了新歌单")
}

/**
 * An update from a followed artist, e.g., a new album release.
 */
@Serializable
data class ArtistUpdate(
    override val id: String,
    override val timestamp: Long,
    val artist: Artist,
    val album: Album
) : Feed()

/**
 * A recommendation from the system, e.g., a featured playlist.
 */
@Serializable
data class SystemRecommendation(
    override val id: String,
    override val timestamp: Long,
    val playlist: Playlist
) : Feed()

/**
 * A sealed interface to represent content that can be shared in a post or activity.
 */
@Serializable
sealed interface ShareableContent {
    @Serializable
    data class SharedSong(val song: Song) : ShareableContent
    @Serializable
    data class SharedPlaylist(val playlist: Playlist) : ShareableContent
    @Serializable
    data class SharedAlbum(val album: Album) : ShareableContent
}
