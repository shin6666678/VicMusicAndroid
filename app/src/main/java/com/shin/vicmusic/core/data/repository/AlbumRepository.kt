package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.AlbumDetail
import com.shin.vicmusic.core.domain.PageResult
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.model.request.AlbumDetailReq
import com.shin.vicmusic.core.model.request.AlbumPageReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
) {
    suspend fun getAlbums(pageReq: AlbumPageReq): MyNetWorkResult<NetworkPageData<Album>> {
        val dtoResponse=datasource.getAlbums(pageReq)
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


    suspend fun getAlbumDetail(albumDetailReq: AlbumDetailReq): MyNetWorkResult<AlbumDetail> {
        val resp= datasource.getAlbumById(albumDetailReq)
        if (resp.code == 0 && resp.data != null) {
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
            return MyNetWorkResult.Success(AlbumDetail(albumDomain, songPageResult))
        }
        return MyNetWorkResult.Error(resp.message ?: "未知错误")
    }

}