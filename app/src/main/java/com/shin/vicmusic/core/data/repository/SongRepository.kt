package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.RecommendCard
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.api.SongListItemDto
import com.shin.vicmusic.core.model.request.SongPageReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.network.datasource.MyMockDatasource
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource,
    private val mockDatasource: MyMockDatasource
) {
    suspend fun getSongs(pageReq: SongPageReq): MyNetWorkResult<NetworkPageData<Song>> {
        val dtoResponse = datasource.songs(pageReq)

        // 如果成功，进行数据转换
        if (dtoResponse.code == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            // 使用 Mapper 扩展函数转换
            val domainList = dtoList.map { it.toDomain() }

            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            return MyNetWorkResult.Success(domainData)
        }
        // 失败则原样返回错误信息
        return MyNetWorkResult.Error(dtoResponse.message ?: "未知错误")
    }

    // 歌曲详情
    suspend fun getSongDetail(id: String): MyNetWorkResult<Song> {
        val dtoResponse = datasource.songDetail(id)

        if (dtoResponse.code == 0 && dtoResponse.data != null) {
            return MyNetWorkResult.Success(dtoResponse.data.toDomain())
        }
        return MyNetWorkResult.Error(dtoResponse.message ?: "未知错误")
    }

    suspend fun playSong(id: String) {
        datasource.playSong(id)
    }

    /**
     * 获取每日推荐
     */
    suspend fun getDailyRecommendSongs(): MyNetWorkResult<List<SongListItemDto>> {
        val response = datasource.getDailyRecommendSongs()
        return if (response.code == 0 && response.data != null) {
            MyNetWorkResult.Success(response.data)
        } else {
            MyNetWorkResult.Error(response.message ?: "未知错误")
        }
    }

    suspend fun getAlsoListening(): MyNetWorkResult<RecommendCard> {
        val response = datasource.getAlsoListening()
        // 2. 判断并转换
        return if (response.code == 0 && response.data != null) {
            val domainData = response.data.toDomain()
            MyNetWorkResult.Success(domainData)
        } else {
            MyNetWorkResult.Error(response.message ?: "未知错误")
        }
    }


}
