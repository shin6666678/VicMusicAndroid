package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.request.LikeSongReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject

class LikeRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
)  {
    suspend fun likedSongs(): Result<NetworkPageData<Song>> {
        val dtoResponse =datasource.likedSongs()

        if (dtoResponse.code == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            val domainList = dtoList.map { it.toDomain() }
            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            return Result.Success(domainData)
        }
        return Result.Error(dtoResponse.message ?: "未知错误")
    }

    suspend fun likeSong(id: String): Result<Unit> {
        val dtoResponse = datasource.likeSong(LikeSongReq(id))
        if (dtoResponse.code == 0) {
            return Result.Success(Unit)
        }
        return Result.Error(dtoResponse.message ?: "操作失败")
    }
}