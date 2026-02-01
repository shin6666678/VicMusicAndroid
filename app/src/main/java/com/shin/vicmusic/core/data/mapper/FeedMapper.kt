package com.shin.vicmusic.core.data.mapper

import com.shin.vicmusic.core.domain.Feed
import com.shin.vicmusic.core.domain.ShareableContent
import com.shin.vicmusic.core.domain.UserActivity
import com.shin.vicmusic.core.domain.UserPost
import com.shin.vicmusic.core.model.api.FeedItemDto

fun FeedItemDto.toFeed(): Feed {
    val shareableContent = when (content?.type) {
        "sharedSong" -> ShareableContent.SharedSong(content.song!!)
        "sharedPlaylist" -> ShareableContent.SharedPlaylist(content.playlist!!)
        "sharedAlbum" -> ShareableContent.SharedAlbum(content.album!!)
        else -> null
    }

    return if (activityType != null && shareableContent != null) {
        UserActivity(
            id = id,
            timestamp = timestamp,
            likesCount = likeCount ?: 0,
            isLiked = isLiked ?: false,
            user = user,
            activityType = activityType,
            content = shareableContent
        )
    } else {
        UserPost(
            id = id,
            timestamp = timestamp,
            likesCount = likeCount ?: 0,
            isLiked = isLiked ?: false,
            user = user,
            content = shareableContent,
            comment = comment
        )
    }
}
