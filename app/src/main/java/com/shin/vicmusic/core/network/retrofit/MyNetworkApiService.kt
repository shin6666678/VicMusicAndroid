package com.shin.vicmusic.core.network.retrofit

import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.User
import com.shin.vicmusic.core.model.api.SongDetailDto
import com.shin.vicmusic.core.model.api.SongListItemDto
import com.shin.vicmusic.core.model.request.FollowReq
import com.shin.vicmusic.core.model.request.LikeSongReq
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
 * 修改：将 Song 实体替换为 API 层的 DTO (SongListItemDTO, SongDetailDTO)
 */
interface MyNetworkApiService {

    @GET("/api/songs/v1/page")
    suspend fun songs(): NetworkResponse<NetworkPageData<SongListItemDto>>

    @GET("/api/songs/v1/{id}")
    suspend fun songDetail(@Path(value = "id") id: String): NetworkResponse<SongDetailDto>

    @GET("/api/notify/v1/send_code")
    suspend fun mailCode(
        @Query(value = "to") to: String,
        @Query(value = "captcha") captcha: String
    ): NetworkResponse<Unit>

    @POST("/api/user/v1/register")
    suspend fun register(@Body req: UserRegisterReq): NetworkResponse<User>

    // [新增] 登录接口
    @POST("/api/user/v1/login")
    suspend fun login(@Body req: UserLoginReq): NetworkResponse<String>

    // [新增] 获取用户信息接口 (Info Interface)
    @GET("/api/user/v1/info")
    suspend fun userInfo(): NetworkResponse<User>

    // [新增] 获取喜欢歌曲列表接口
    @GET("/api/like/v1/listSong")
    suspend fun getLikedSongs(): NetworkResponse<NetworkPageData<SongListItemDto>>

    // [新增] 喜欢歌曲接口
    @POST("/api/like/v1/likeSong")
    suspend fun likeSong(@Body req: LikeSongReq): NetworkResponse<Unit>

    @GET("/api/artist/v1/page")
    suspend fun getArtists(
        @Query(value = "region") region: String,
        @Query(value = "type")type: String,
        @Query(value = "style")style: String
    ): NetworkResponse<NetworkPageData<Artist>>

    @GET("/api/artist/v1/{id}")
    suspend fun getArtistById(@Path(value = "id") id: String): NetworkResponse<Artist>

    @POST("/api/relationship/v1/follow")
    suspend fun follow(@Body req: FollowReq): NetworkResponse<Unit>

    @GET("/api/songs/v1/page")
    fun getSongsByArtistId(@Query(value = "artistId") artistId: String): NetworkResponse<NetworkPageData<SongListItemDto>>

}