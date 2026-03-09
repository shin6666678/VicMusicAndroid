package com.shin.vicmusic.core.data.repository

import android.R.attr.data
import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.RecentPlayCount
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.api.RecentPlayCountDto
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
) {
    suspend fun getHistory(): MyNetWorkResult<List<Song>> {
        val res = datasource.getHistory()
        if (res.code == 0) {
            val list = res.data?.map { it.toDomain() } ?: emptyList()
            return MyNetWorkResult.Success(list)
        }
        return MyNetWorkResult.Error(res.message?: "未知错误")
    }

    suspend fun getRecentPlayCount(): MyNetWorkResult<RecentPlayCount> {
        val res = datasource.getRecentPlayCount()
        if (res.code == 0) {
            val data = res.data?.toDomain() ?: RecentPlayCount(0,"")
            return MyNetWorkResult.Success(data)
        }
        return MyNetWorkResult.Error(res.message?: "未知错误")
    }
}