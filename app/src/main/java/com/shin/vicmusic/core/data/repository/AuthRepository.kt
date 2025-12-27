package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.core.model.request.UserLoginReq
import com.shin.vicmusic.core.model.request.UserRegisterReq
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
) {
    // 登录(Login)
    suspend fun login(req: UserLoginReq) = datasource.login(req)

    suspend fun mailCode(to:String, captcha:String)=datasource.mailCode(to,captcha)
    // 注册(Register)
    suspend fun register(req: UserRegisterReq) = datasource.register(req)

    // 获取用户信息(Get User Info)
    suspend fun getUserInfo(): Result<UserInfo> {
        val dtoResponse = datasource.userInfo()
        if(dtoResponse.status == 0 && dtoResponse.data != null){
            val domainUser = dtoResponse.data.toDomain()
            return Result.Success(domainUser)
        }
        return Result.Error(dtoResponse.message ?: "未知错误")
    }
}