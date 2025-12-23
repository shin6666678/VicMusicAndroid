package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.AlbumDetail
import com.shin.vicmusic.core.domain.PageResult
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.request.AlbumDetailReq
import com.shin.vicmusic.core.model.request.AlbumPageReq
import com.shin.vicmusic.core.model.request.SongPageReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.model.response.NetworkResponse
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
) {
    suspend fun getAlbums(pageReq: AlbumPageReq): Result<NetworkPageData<Album>> {
        val dtoResponse=datasource.getAlbums(pageReq)
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


    suspend fun getAlbumDetail(albumDetailReq: AlbumDetailReq): Result<AlbumDetail> {
        val resp= datasource.getAlbumById(albumDetailReq)
        if (resp.status == 0 && resp.data != null) {
            val dto = resp.data
            val albumDomain=dto.toDomain()

            val songPageDto = dto.songs
            val songPageResult = if (songPageDto != null) {
                PageResult(
                    items = songPageDto.list?.map { it.toDomain() } ?: emptyList(),
                    total = songPageDto.pagination.total,
                    page = songPageDto.pagination.page,
                    hasMore = songPageDto.pagination.page < songPageDto.pagination.pages
                )
            } else {
                // 如果后端没返回 songs，给个空页
                PageResult(emptyList(), 0, 1, false)
            }
            return Result.Success(AlbumDetail(albumDomain, songPageResult))
        }
        return Result.Error(resp.message ?: "未知错误")
    }

}