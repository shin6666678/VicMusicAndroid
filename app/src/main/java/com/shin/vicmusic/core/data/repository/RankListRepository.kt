package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.network.datasource.MyMockDatasource
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject

class RankListRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource,
    private val mockDatasource: MyMockDatasource
) {
    // 获取排行榜数据
    suspend fun getRankListPeeks() = mockDatasource.getRankListPeeks()

    // 获取排行榜详情
    suspend fun getRankListById(id: String) = mockDatasource.getRankListById(id)
}