package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.model.request.FollowReq
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
){
    //关注/取消关注
    suspend fun follow(targetId:String,targetType:Int) = datasource.follow(FollowReq(targetId,targetType))
}