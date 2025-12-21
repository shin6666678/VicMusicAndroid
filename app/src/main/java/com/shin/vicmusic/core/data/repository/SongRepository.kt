package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.model.request.LikeSongReq
import com.shin.vicmusic.core.network.datasource.MyMockDatasource
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.emptyList

@Singleton
class SongRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource,
    private val mockDatasource: MyMockDatasource
) {
    // 发现页数据
    suspend fun getSongs() = datasource.songs()

    // 歌曲详情
    suspend fun getSongDetail(id: String) = datasource.songDetail(id)

    // 喜欢/取消喜欢歌曲
    suspend fun likeSong(id: String) = datasource.likeSong(LikeSongReq(id))

    // 获取喜欢列表
    suspend fun getLikedSongs() = datasource.likedSongs()

    // 根据歌手id获取歌曲
    suspend fun getSongsByArtistId(artistId: String) = mockDatasource.getSongsByArtistId(artistId)


}
