package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toFeed
import com.shin.vicmusic.core.domain.Feed
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.model.request.PublishFeedReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
) {

    suspend fun getFeeds(page: Int, size: Int): Result<NetworkPageData<Feed>> {
        val dtoResponse = datasource.getFeeds(page, size)

        return if (dtoResponse.code == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            val domainList = dtoList.map { it.toFeed() }
            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            Result.Success(domainData)
        } else {
            Result.Error(dtoResponse.message ?: "获取动态列表失败")
        }
    }

    suspend fun getFollowingFeeds(page: Int, size: Int): Result<NetworkPageData<Feed>> {
        val dtoResponse = datasource.getFollowingFeeds(page, size)

        return if (dtoResponse.code == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            val domainList = dtoList.map { it.toFeed() }
            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            Result.Success(domainData)
        } else {
            Result.Error(dtoResponse.message ?: "获取关注动态失败")
        }
    }

    suspend fun getUserFeeds(userId: String, page: Int, size: Int): Result<NetworkPageData<Feed>> {
        val dtoResponse = datasource.getUserFeeds(userId, page, size)

        return if (dtoResponse.code == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            val domainList = dtoList.map { it.toFeed() }
            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            Result.Success(domainData)
        } else {
            Result.Error(dtoResponse.message ?: "获取用户动态失败")
        }
    }

    suspend fun publishFeed(req: PublishFeedReq): Result<String> {
        val response = datasource.publishFeed(req)
        return if (response.code == 0) {
            val resp = response.data ?: ""
            Result.Success(resp)
        } else {
            Result.Error(response.message ?: "发布失败")
        }
    }
}
