package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.User
import com.shin.vicmusic.core.model.request.FollowReq
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
){
    //关注/取消关注
    suspend fun follow(targetId:String,targetType:Int):Result<Unit> {
        val dtoResponse = datasource.follow(FollowReq(targetId,targetType))
        if (dtoResponse.status == 0) {
            return com.shin.vicmusic.core.domain.Result.Success(Unit)
        }
        return Result.Error(dtoResponse.message ?: "操作失败")
    }

    suspend fun getFollowedUsers(): Result<List<User>> {
        val res = datasource.getFollowedUsers()
        if (res.status == 0) {
            // 使用现有的 Mapper 转换 (Use existing Mapper)
            val list = res.data?.map { it.toDomain() } ?: emptyList()
            return Result.Success(list)
        }
        return Result.Error(res.message?: "操作失败")
    }
}