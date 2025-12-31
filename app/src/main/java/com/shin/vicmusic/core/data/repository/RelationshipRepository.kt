package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.PageResult
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.core.model.request.FollowReq
import com.shin.vicmusic.core.network.datasource.MyMockDatasource
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject

class RelationshipRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource,
    private val mockDatasource: MyMockDatasource
) {
    suspend fun getFollowedUser(page: Int, size: Int): Result<PageResult<UserInfo>> {
        return try {
            val response = datasource.getFollowedUsers()
            if (response.code == 0 && response.data != null) {
                val pageData = response.data
                val total = pageData.pagination.total
                // [Fix] 计算 hasMore
                val hasMore = (page * size) < total

                Result.Success(
                    PageResult(
                        items = pageData.list?.map { it.toDomain() } ?: emptyList(),
                        total = total,
                        page = pageData.pagination.page,
                        hasMore = hasMore
                    )
                )
            } else {
                Result.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }
    suspend fun getFollowedArtists(page: Int, size: Int): Result<PageResult<Artist>> {
        return try {
            val response = datasource.getFollowedArtists()
            if (response.code == 0 && response.data != null) {
                val pageData = response.data
                val total = pageData.pagination.total
                val hasMore = (page * size) < total

                Result.Success(
                    PageResult(
                        items = pageData.list?.map { it.toDomain() } ?: emptyList(),
                        total = total,
                        page = pageData.pagination.page,
                        hasMore = hasMore
                    )
                )
            } else {
                Result.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    suspend fun getFans(page: Int, size: Int): Result<PageResult<UserInfo>> {
        return try {
            val response = datasource.getFans()
            if (response.code == 0 && response.data != null) {
                val pageData = response.data
                val total = pageData.pagination.total
                // [Fix] 计算 hasMore
                val hasMore = (page * size) < total

                Result.Success(
                    PageResult(
                        items = pageData.list?.map { it.toDomain() } ?: emptyList(),
                        total = total,
                        page = pageData.pagination.page,
                        hasMore = hasMore
                    )
                )
            } else {
                Result.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }
    suspend fun getFriends(page: Int, size: Int): Result<PageResult<UserInfo>> {
        return Result.Success(PageResult(emptyList(), 0, 1, false))
    }

    suspend fun follow(targetId:String,targetType:Int):Result<Unit> {
        val dtoResponse = datasource.follow(FollowReq(targetId,targetType))
        if (dtoResponse.code == 0) {
            return Result.Success(Unit)
        }
        return Result.Error(dtoResponse.message ?: "操作失败")
    }
}