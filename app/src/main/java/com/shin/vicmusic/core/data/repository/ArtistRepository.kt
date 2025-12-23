package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.model.request.ArtistPageReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ArtistRepository@Inject constructor(
    private val datasource: MyRetrofitDatasource
){
    // 透传筛选参数到数据源
    suspend fun getArtists(artistPageReq: ArtistPageReq): Result<NetworkPageData<Artist>> {
        val dtoResponse = datasource.getArtists(artistPageReq)

        if (dtoResponse.status == 0 && dtoResponse.data != null) {
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

    suspend fun getArtistDetailById(artistId:String):Result<Artist>{
        val dtoResponse = datasource.getArtistById(artistId)
        if (dtoResponse.status == 0 && dtoResponse.data != null) {
            return Result.Success(dtoResponse.data.toDomain())
        }
        return Result.Error(dtoResponse.message ?: "未知错误")
    }
}
