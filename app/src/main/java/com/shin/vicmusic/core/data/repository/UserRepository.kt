package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.UserInfo
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
        if (dtoResponse.code == 0) {
            return Result.Success(Unit)
        }
        return Result.Error(dtoResponse.message ?: "操作失败")
    }

    suspend fun getFollowedUsers(): Result<List<UserInfo>> {
        val res = datasource.getFollowedUsers()
        if (res.code == 0) {
            val list = res.data?.map { it.toDomain() } ?: emptyList()
            return Result.Success(list)
        }
        return Result.Error(res.message?: "操作失败")
    }

    suspend fun getFans(): Result<List<UserInfo>> {
        val res = datasource.getFans()
        if (res.code == 0) {
            val list = res.data?.map { it.toDomain() } ?: emptyList()
            return Result.Success(list)
        }
        return Result.Error(res.message?: "操作失败")
    }

    // 签到 (Check-in)
    suspend fun checkIn(): Result<String> {
        val response = datasource.checkIn()
        return if (response.code == 0) {
            // 成功时返回消息 (Return message on success)
            Result.Success(response.data.toString()) // 实际上 data 可能是 null，这里取 msg 更好，但在 Result 封装里通常 Success 携带数据
            // 这里为了简单，我们假设 ViewModel 会重新拉取用户信息
            // 或者我们可以修改 NetworkResponse 解析逻辑
            // 简单处理：
            Result.Success("签到成功")
        } else {
            Result.Error(response.message?:"签到失败")
        }
    }

    // 上报时长 (Report duration)
    suspend fun reportDuration(seconds: Int): Result<Unit> {
        val response = datasource.reportDuration(seconds)
        return if (response.code == 0) {
            Result.Success(Unit)
        } else {
            Result.Error(response.message?:"上报时长失败")
        }
    }
}