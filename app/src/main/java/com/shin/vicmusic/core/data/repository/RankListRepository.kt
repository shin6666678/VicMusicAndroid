package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.RankListDetail
import com.shin.vicmusic.core.domain.RankListPeak
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.network.datasource.MyMockDatasource
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject


class RankListRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource,
    private val mockDatasource: MyMockDatasource
) {
    // 获取排行榜数据
    suspend fun getRankListPeeks(): Result<List<RankListPeak>> {
        val response = datasource.getRankListPeeks()
        if (response.status == 0 && response.data != null) {
            return Result.Success(response.data.map { it.toDomain() })
        }
        return Result.Error(response.message ?: "获取榜单失败")
    }

    // 获取排行榜详情
    suspend fun getRankListById(id: String): Result<RankListDetail> {
        val response = datasource.rankListDetail(id)
        if (response.status == 0 && response.data != null) {
            return Result.Success(response.data.toDomain())
        }
        return Result.Error(response.message ?: "请求失败(Request Failed)")
    }
}