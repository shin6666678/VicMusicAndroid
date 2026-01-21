package com.shin.vicmusic.feature.feed

import androidx.lifecycle.ViewModel
import com.shin.vicmusic.core.domain.ActivityType
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.ArtistUpdate
import com.shin.vicmusic.core.domain.Feed
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.ShareableContent
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.domain.SystemRecommendation
import com.shin.vicmusic.core.domain.User
import com.shin.vicmusic.core.domain.UserActivity
import com.shin.vicmusic.core.domain.UserPost
import com.shin.vicmusic.core.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    authManager: AuthManager
) : ViewModel() {

    // --- UI State ---
    private val _discoveryItems = MutableStateFlow<List<Feed>>(emptyList())
    val discoveryItems = _discoveryItems.asStateFlow()

    private val _followingItems = MutableStateFlow<List<Feed>>(emptyList())
    val followingItems = _followingItems.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex = _selectedTabIndex.asStateFlow()

    val currentUser = authManager.currentUser

    private val _headerBackgroundImage = MutableStateFlow("")
    val headerBackgroundImage = _headerBackgroundImage.asStateFlow()

    init {
        loadMockData()
    }

    fun updateTab(index: Int) {
        _selectedTabIndex.value = index
    }

    private fun loadMockData() {
        // --- Mock Data Definitions ---
        val mockUser1 = User(id = "user1", name = "音乐爱好者小明", headImg = "https://picsum.photos/id/237/200")
        val mockUser2 = User(id = "user2", name = "Lofi女孩", headImg = "https://picsum.photos/id/1027/200")
        val mockArtist = Artist(id = "artist2", name = "Taylor Swift", image = "https://picsum.photos/id/1067/200")
        
        // Set current user and header background
        _headerBackgroundImage.value = "https://picsum.photos/id/1018/800/600"

        val mockSong = Song(
            id = "song1", title = "晴天", artist = Artist(id = "artist1", name = "周杰伦"),
            album = Album(id = "album1", title = "叶惠美"), icon = "https://p2.music.126.net/c-s-CiMM-2s-42dJ-2b-gA==/109951163135492429.jpg",
            likesCount = 123, commentsCount = 45
        )
        val mockPlaylist = Playlist(
            id = "playlist1", name = "我的学习专用BGM", cover = "https://p2.music.126.net/5-l0e-G_z-Z6g-Z6g-Z6gQ==/109951163135492429.jpg",
            ownerName = mockUser2.name, likeCount = 55, description = ""
        )
        val newAlbum = Album(id = "album2", title = "Midnights", artist = mockArtist, artistName = mockArtist.name, icon = "https://picsum.photos/id/1084/200")

        // --- Following Feed Items ---
        _followingItems.value = listOf(
            UserPost(
                id = "post1", timestamp = System.currentTimeMillis() - 1000 * 60 * 5, user = mockUser1,
                content = ShareableContent.SharedSong(mockSong), comment = "这首歌的前奏一响，我的青春就回来了！单曲循环预定！☀️"
            ),
            UserActivity(
                id = "activity1", timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2, user = mockUser2,
                activityType = ActivityType.CREATED_PLAYLIST, content = ShareableContent.SharedPlaylist(mockPlaylist)
            ),
            ArtistUpdate(
                id = "update1", timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24, artist = mockArtist, album = newAlbum
            )
        )

        // --- Discovery Feed Items ---
        val recommendedPlaylist = Playlist(
            id = "playlist2", name = "【宝藏】听了就会上瘾的欧美旋律", cover = "https://picsum.photos/id/129/300/300",
            ownerName = "VicMusic官方", likeCount = 1890, description = ""
        )
        _discoveryItems.value = listOf(
            SystemRecommendation(
                id = "rec1", timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 8, playlist = recommendedPlaylist
            )
        ) + _followingItems.value.shuffled() // Mix in some following items for variety
    }
}