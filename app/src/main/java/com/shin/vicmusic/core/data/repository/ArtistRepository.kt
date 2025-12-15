package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.domain.Artist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface ArtistRepository {
    fun getArtists(): Flow<List<Artist>>
    fun getArtistById(artistId: String): Flow<Artist?>
    fun followArtist(artistId: String): Flow<Boolean>
    fun unfollowArtist(artistId: String): Flow<Boolean>
}

@Singleton
class ArtistRepositoryImpl @Inject constructor() : ArtistRepository {
    override fun getArtists(): Flow<List<Artist>> = flow {
        // 模拟数据
        val artists = listOf(
            Artist(
                id = "1",
                name = "周杰伦",
                image = "https://example.com/jay_chou.jpg",
                description = "华语流行乐男歌手、词曲创作人、制作人、MV及电影导演、编剧。",
                followerCount = 1000000,
                isFollowing = true,
                region = "港台",
                type = "男",
                style = "流行"
            ),
            Artist(
                id = "2",
                name = "林俊杰",
                image = "https://example.com/jj_lin.jpg",
                description = "新加坡籍华语流行音乐男歌手、词曲创作者、音乐制作人。",
                followerCount = 800000,
                isFollowing = false,
                region = "港台",
                type = "男",
                style = "流行"
            ),
            Artist(
                id = "3",
                name = "G.E.M. 邓紫棋",
                image = "https://example.com/gem.jpg",
                description = "中国香港创作型女歌手。",
                followerCount = 900000,
                isFollowing = true,
                region = "港台",
                type = "女",
                style = "流行"
            ),
            Artist(
                id = "4",
                name = "陈奕迅",
                image = "https://example.com/eason_chan.jpg",
                description = "香港男歌手、演员。",
                followerCount = 700000,
                isFollowing = false,
                region = "港台",
                type = "男",
                style = "流行"
            ),
            Artist(
                id = "5",
                name = "薛之谦",
                image = "https://example.com/joker_xue.jpg",
                description = "中国内地流行乐男歌手、音乐制作人。",
                followerCount = 600000,
                isFollowing = false,
                region = "内地",
                type = "男",
                style = "流行"
            ),
            Artist(
                id = "6",
                name = "王力宏",
                image = "https://example.com/leehom_wang.jpg",
                description = "华语流行乐男歌手、音乐制作人、演员、导演。",
                followerCount = 500000,
                isFollowing = true,
                region = "港台",
                type = "男",
                style = "流行"
            )
        )
        emit(artists)
    }

    override fun getArtistById(artistId: String): Flow<Artist?> = flow {
        // 模拟数据
        getArtists().collect { artists ->
            emit(artists.find { it.id == artistId })
        }
    }

    override fun followArtist(artistId: String): Flow<Boolean> = flow {
        // 模拟成功
        emit(true)
    }

    override fun unfollowArtist(artistId: String): Flow<Boolean> = flow {
        // 模拟成功
        emit(true)
    }
}
