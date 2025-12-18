package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.model.request.UserLoginReq
import com.shin.vicmusic.core.model.request.UserRegisterReq
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import retrofit2.http.Query
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
    suspend fun getUserInfo() = datasource.userInfo()
}