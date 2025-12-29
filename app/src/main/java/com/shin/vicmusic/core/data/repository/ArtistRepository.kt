package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.ArtistDetail
import com.shin.vicmusic.core.domain.PageResult
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.model.request.ArtistDetailReq
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

    suspend fun getArtistDetail(artistDetailReq: ArtistDetailReq): Result<ArtistDetail>{
        val resp= datasource.getArtistById(artistDetailReq)
        if (resp.code == 0 && resp.data != null) {
            val dto = resp.data
            val artistDomain=dto.toDomain()

            val songPageDto = dto.songs
            val songPageResult = if (songPageDto != null) {
                PageResult(
                    items = songPageDto.list?.map { it.toDomain() } ?: emptyList(),
                    total = songPageDto.pagination.total,
                    page = songPageDto.pagination.page,
                    hasMore = songPageDto.pagination.page < songPageDto.pagination.pages
                )
            } else {
                PageResult(emptyList(), 0, 1, false)
            }
            return Result.Success(ArtistDetail(artistDomain, songPageResult))
        }
        return Result.Error(resp.message ?: "未知错误")
    }

    suspend fun getFollowedArtists(): Result<List<Artist>> {
        val res = datasource.getFollowedArtists()
        if (res.code == 0) {
            val list = res.data?.map { it.toDomain() } ?: emptyList()
            return Result.Success(list)
        }
        return Result.Error(res.message ?: "未知错误")
    }

}
