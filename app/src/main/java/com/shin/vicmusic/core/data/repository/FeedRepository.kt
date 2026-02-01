package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toFeed
import com.shin.vicmusic.core.domain.Feed
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.model.request.PublishFeedReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
) {

    suspend fun getFeeds(page: Int, size: Int): MyNetWorkResult<NetworkPageData<Feed>> {
        val dtoResponse = datasource.getFeeds(page, size)

        return if (dtoResponse.code == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            val domainList = dtoList.map { it.toFeed() }
            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            MyNetWorkResult.Success(domainData)
        } else {
            MyNetWorkResult.Error(dtoResponse.message ?: "获取动态列表失败")
        }
    }

    suspend fun getFollowingFeeds(page: Int, size: Int): MyNetWorkResult<NetworkPageData<Feed>> {
        val dtoResponse = datasource.getFollowingFeeds(page, size)

        return if (dtoResponse.code == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            val domainList = dtoList.map { it.toFeed() }
            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            MyNetWorkResult.Success(domainData)
        } else {
            MyNetWorkResult.Error(dtoResponse.message ?: "获取关注动态失败")
        }
    }

    suspend fun getUserFeeds(userId: String, page: Int, size: Int): MyNetWorkResult<NetworkPageData<Feed>> {
        val dtoResponse = datasource.getUserFeeds(userId, page, size)

        return if (dtoResponse.code == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            val domainList = dtoList.map { it.toFeed() }
            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            MyNetWorkResult.Success(domainData)
        } else {
            MyNetWorkResult.Error(dtoResponse.message ?: "获取用户动态失败")
        }
    }

    suspend fun publishFeed(req: PublishFeedReq): MyNetWorkResult<String> {
        val response = datasource.publishFeed(req)
        return if (response.code == 0) {
            val resp = response.data ?: ""
            MyNetWorkResult.Success(resp)
        } else {
            MyNetWorkResult.Error(response.message ?: "发布失败")
        }
    }

    suspend fun toggleLikeFeed(feedId: String): MyNetWorkResult<Unit> {
        val response = datasource.likeFeed(feedId)
        return if (response.code == 0) {
            MyNetWorkResult.Success(Unit)
        } else {
            MyNetWorkResult.Error(response.message ?: "操作失败")
        }
    }
}
