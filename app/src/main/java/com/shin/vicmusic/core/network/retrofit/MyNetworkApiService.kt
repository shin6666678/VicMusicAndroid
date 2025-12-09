package com.shin.vicmusic.core.network.retrofit

import com.shin.vicmusic.core.model.Song
import com.shin.vicmusic.core.model.User
import com.shin.vicmusic.core.model.request.UserLoginReq
import com.shin.vicmusic.core.model.request.UserRegisterReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.model.response.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 网络请求接口
 */
interface MyNetworkApiService {

    @GET("/api/songs/v1/page")
    suspend fun songs(): NetworkResponse<NetworkPageData<Song>>

    @GET("/api/songs/v1/{id}")
    suspend fun songDetail(@Path(value = "id") id:String,): NetworkResponse<Song>

    @GET("/api/notify/v1/send_code")
    suspend fun mailCode(@Query(value = "to")to:String,@Query(value = "captcha") captcha:String): NetworkResponse<Unit>

    @POST("/api/user/v1/register")
    suspend fun register(@Body req: UserRegisterReq):NetworkResponse<User>

    // [新增] 登录接口
    @POST("/api/user/v1/login")
    suspend fun login(@Body req: UserLoginReq): NetworkResponse<String>

    // [新增] 获取喜欢歌曲列表接口
    @GET("/api/like/v1/listSong")
    suspend fun getLikedSongs(): NetworkResponse<NetworkPageData<Song>>
}