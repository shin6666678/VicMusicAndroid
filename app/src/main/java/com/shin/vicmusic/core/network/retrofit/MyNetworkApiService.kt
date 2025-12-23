package com.shin.vicmusic.core.network.retrofit

import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.User
import com.shin.vicmusic.core.model.api.AlbumDetailResp
import com.shin.vicmusic.core.model.api.AlbumDto
import com.shin.vicmusic.core.model.api.ArtistDto
import com.shin.vicmusic.core.model.api.SongDetailDto
import com.shin.vicmusic.core.model.api.SongListItemDto
import com.shin.vicmusic.core.model.api.UserInfoDto
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
    /*
    用户
     */
    @GET("/api/notify/v1/send_code")
    suspend fun mailCode(
        @Query(value = "to") to: String,
        @Query(value = "captcha") captcha: String
    ): NetworkResponse<Unit>

    @POST("/api/user/v1/register")
    suspend fun register(@Body req: UserRegisterReq): NetworkResponse<User>

    // 登录接口
    @POST("/api/user/v1/login")
    suspend fun login(@Body req: UserLoginReq): NetworkResponse<String>

    // 获取用户信息接口 (Info Interface)
    @GET("/api/user/v1/info")
    suspend fun userInfo(): NetworkResponse<UserInfoDto>

    /*
    song
     */
    @GET("/api/songs/v1/page")
    suspend fun songs(
        @Query("artistId") artistId: String?,
        @Query("albumId") albumId: String?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): NetworkResponse<NetworkPageData<SongListItemDto>>

    @GET("/api/songs/v1/{id}")
    suspend fun songDetail(@Path(value = "id") id: String): NetworkResponse<SongDetailDto>

    /*
    喜欢
     */
    // 获取喜欢歌曲列表接口
    @GET("/api/like/v1/listSong")
    suspend fun getLikedSongs(): NetworkResponse<NetworkPageData<SongListItemDto>>

    // [新增] 喜欢歌曲接口
    @POST("/api/like/v1/likeSong")
    suspend fun likeSong(@Body req: LikeSongReq): NetworkResponse<Unit>

    /*
    Artist艺术家
     */
    @GET("/api/artist/v1/page")
    suspend fun getArtists(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query(value = "region") region: String,
        @Query(value = "type") type: String,
        @Query(value = "style") style: String
    ): NetworkResponse<NetworkPageData<ArtistDto>>

    @GET("/api/artist/v1/{id}")
    suspend fun getArtistById(@Path(value = "id") id: String): NetworkResponse<ArtistDto>


    /*
    Album专辑
    */
    @GET("/api/album/v1/page")
    suspend fun getAlbums(
        @Query("page") page: Int?=null,
        @Query("size") size: Int?=null,
        @Query("artistId") artistId:String?=null,
    ): NetworkResponse<NetworkPageData<AlbumDto>>

    @GET("/api/album/v1/detail")
    suspend fun getAlbumById(
        @Query("id") id: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): NetworkResponse<AlbumDetailResp>

    /*
    社交关系
     */
    @POST("/api/relationship/v1/follow")
    suspend fun follow(@Body req: FollowReq): NetworkResponse<Unit>


}