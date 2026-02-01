package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.request.LikeReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject

class LikeRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
)  {
    suspend fun likedSongs(): MyNetWorkResult<NetworkPageData<Song>> {
        val dtoResponse =datasource.likedSongs()

        if (dtoResponse.code == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            val domainList = dtoList.map { it.toDomain() }
            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            return MyNetWorkResult.Success(domainData)
        }
        return MyNetWorkResult.Error(dtoResponse.message ?: "未知错误")
    }

    suspend fun likedAlbums(): MyNetWorkResult<NetworkPageData<Album>> {
        val dtoResponse = datasource.likedAlbums()
        if (dtoResponse.code == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            // 假设 AlbumDto 也有 toDomain() 扩展方法
            val domainList = dtoList.map { it.toDomain() }
            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            return MyNetWorkResult.Success(domainData)
        }
        return MyNetWorkResult.Error(dtoResponse.message ?: "获取专辑失败")
    }

    suspend fun likedPlaylists(): MyNetWorkResult<NetworkPageData<Playlist>> {
        val dtoResponse = datasource.likedPlaylists()
        if (dtoResponse.code == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            // 假设 PlaylistDto 也有 toDomain() 扩展方法
            val domainList = dtoList.map { it.toDomain() }
            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            return MyNetWorkResult.Success(domainData)
        }
        return MyNetWorkResult.Error(dtoResponse.message ?: "获取歌单失败")
    }

    suspend fun toggleLike(id: String,type:Int): MyNetWorkResult<Boolean> {
        val dtoResponse = datasource.toggleLike(LikeReq(id,type))
        if (dtoResponse.code == 0) {
            return MyNetWorkResult.Success(true)
        }
        return MyNetWorkResult.Error(dtoResponse.message ?: "操作失败")
    }

}