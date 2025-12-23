package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.request.SongPageReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.model.response.NetworkResponse
import com.shin.vicmusic.core.network.datasource.MyMockDatasource
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource,
    private val mockDatasource: MyMockDatasource
) {
    // 发现页数据
    suspend fun getSongs(pageReq: SongPageReq = SongPageReq()) : NetworkResponse<NetworkPageData<Song>>{
        val dtoResponse=datasource.songs(pageReq)

        // 如果成功，进行数据转换
        if (dtoResponse.status == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            // 使用 Mapper 扩展函数转换
            val domainList = dtoList.map { it.toDomain() }

            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            return NetworkResponse(status = 0, message = dtoResponse.message, data = domainData)
        }
        // 失败则原样返回错误信息
        return NetworkResponse(status = dtoResponse.status, message = dtoResponse.message, data = null)
    }

    // 歌曲详情
    suspend fun getSongDetail(id: String) : NetworkResponse<Song>{
        val dtoResponse=datasource.songDetail(id)

        if (dtoResponse.status == 0 && dtoResponse.data != null) {
            return NetworkResponse(status = 0, message = dtoResponse.message, data = dtoResponse.data.toDomain())
        }
        return NetworkResponse(status = dtoResponse.status, message = dtoResponse.message, data = null)
    }


}
