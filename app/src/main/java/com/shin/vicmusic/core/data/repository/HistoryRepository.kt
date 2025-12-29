package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
) {
    suspend fun getHistory(): Result<List<Song>> {
        val res = datasource.getHistory()
        if (res.code == 0) {
            val list = res.data?.map { it.toDomain() } ?: emptyList()
            return Result.Success(list)
        }
        return Result.Error(res.message?: "未知错误")
    }
}