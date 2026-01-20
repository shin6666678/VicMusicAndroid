package com.shin.vicmusic.feature.feed

import androidx.lifecycle.ViewModel
import com.shin.vicmusic.core.domain.ActivityType
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Feed
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.ShareableContent
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.domain.User
import com.shin.vicmusic.core.domain.UserActivity
import com.shin.vicmusic.core.domain.UserPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor() : ViewModel() {

    private val _feedItems = MutableStateFlow<List<Feed>>(emptyList())
    val feedItems = _feedItems.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val mockUser1 = User(id = "user1", name = "音乐爱好者小明", headImg = "https://picsum.photos/id/237/200")
        val mockUser2 = User(id = "user2", name = "Lofi女孩", headImg = "https://picsum.photos/id/1027/200")

        val mockSong = Song(
            id = "song1",
            title = "晴天",
            artist = Artist(id = "artist1", name = "周杰伦"),
            album = Album(id = "album1", title = "叶惠美"),
            icon = "https://p2.music.126.net/c-s-CiMM-2s-42dJ-2b-gA==/109951163135492429.jpg",
            likesCount = 123,
            commentsCount = 45
        )

        val mockPlaylist = Playlist(
            id = "playlist1",
            name = "我的学习专用BGM",
            cover = "https://p2.music.126.net/5-l0e-G_z-Z6g-Z6g-Z6gQ==/109951163135492429.jpg",
            ownerName = mockUser2.name,
            likeCount = 55,
            description = "一首首动人的旋律，陪你度过安静的学习时光。"
        )

        _feedItems.value = listOf(
            UserPost(
                id = "post1",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 5, // 5 minutes ago
                user = mockUser1,
                content = ShareableContent.SharedSong(mockSong),
                comment = "这首歌的前奏一响，我的青春就回来了！单曲循环预定！☀️"
            ),
            UserActivity(
                id = "activity1",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2, // 2 hours ago
                user = mockUser2,
                activityType = ActivityType.CREATED_PLAYLIST,
                content = ShareableContent.SharedPlaylist(mockPlaylist)
            )
        )
    }
}