package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.request.LikeSongReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.model.response.NetworkResponse
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject

class LikeRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
)  {
    suspend fun likedSongs(): NetworkResponse<NetworkPageData<Song>>{
        val dtoResponse =datasource.likedSongs()

        if (dtoResponse.status == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            val domainList = dtoList.map { it.toDomain() }
            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            return NetworkResponse(status = 0, message = dtoResponse.message, data = domainData)
        }
        return NetworkResponse(status = dtoResponse.status, message = dtoResponse.message, data = null)
    }

    suspend fun likeSong(id: String):NetworkResponse<Unit> {
        return datasource.likeSong(LikeSongReq(id))
    }
}