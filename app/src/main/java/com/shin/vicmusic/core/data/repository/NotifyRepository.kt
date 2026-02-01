package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.model.api.NotifyDto
import com.shin.vicmusic.core.model.request.PageReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject

class NotifyRepository @Inject constructor(
    private val network: MyRetrofitDatasource
) {
    /**
     * 获取通知列表（分页）
     */
    suspend fun getNotifyPage(page: Int, size: Int): MyNetWorkResult<NetworkPageData<NotifyDto>> {
        val result = network.getNotifyPage(PageReq(page = page, size = size))
        return if (result.code == 0 && result.data != null) {
            MyNetWorkResult.Success(result.data)
        } else {
            MyNetWorkResult.Error(result.message?:"")
        }
    }

    /**
     * 获取未读消息计数
     */
    suspend fun getUnreadCount(): MyNetWorkResult<Map<String, Int>> {
        val result = network.getUnreadCount()
        return if (result.code == 0 && result.data != null) {
            MyNetWorkResult.Success(result.data)
        } else {
            MyNetWorkResult.Error(result.message?:"")
        }
    }
}