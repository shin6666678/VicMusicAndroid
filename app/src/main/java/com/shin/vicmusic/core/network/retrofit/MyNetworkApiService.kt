package com.shin.vicmusic.core.network.retrofit

import com.shin.vicmusic.core.domain.User
import com.shin.vicmusic.core.domain.ChatMessage
import com.shin.vicmusic.core.model.api.AlbumDetailResp
import com.shin.vicmusic.core.model.api.AlbumDto
import com.shin.vicmusic.core.model.api.AppUpdateDto
import com.shin.vicmusic.core.model.api.ArtistDetailResp
import com.shin.vicmusic.core.model.api.ArtistDto
import com.shin.vicmusic.core.model.api.ChatSessionDto
import com.shin.vicmusic.core.model.api.CommentDto
import com.shin.vicmusic.core.model.api.FeedItemDto
import com.shin.vicmusic.core.model.api.NotifyDto
import com.shin.vicmusic.core.model.api.PlaylistDetailDto
import com.shin.vicmusic.core.model.api.PlaylistDto
import com.shin.vicmusic.core.model.api.RankListDetailDto
import com.shin.vicmusic.core.model.api.RankListPeakDto
import com.shin.vicmusic.core.model.api.RecommendCardDto
import com.shin.vicmusic.core.model.api.SearchComprehensiveDto
import com.shin.vicmusic.core.model.api.SongDetailDto
import com.shin.vicmusic.core.model.api.SongListItemDto
import com.shin.vicmusic.core.model.api.UserDetailDto
import com.shin.vicmusic.core.model.api.UserDto
import com.shin.vicmusic.core.model.request.ChangeVIPLevelReq
import com.shin.vicmusic.core.model.request.CommentAddReq
import com.shin.vicmusic.core.model.request.FollowReq
import com.shin.vicmusic.core.model.request.LikeReq
import com.shin.vicmusic.core.model.request.PageReq
import com.shin.vicmusic.core.model.request.PlaylistSongReq
import com.shin.vicmusic.core.model.request.PublishFeedReq
import com.shin.vicmusic.core.model.request.UserLoginReq
import com.shin.vicmusic.core.model.request.UserRegisterReq
import com.shin.vicmusic.core.model.request.UserUpdateRequest
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.model.response.NetworkResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

    @POST("/api/user/v1/login")
    suspend fun login(@Body req: UserLoginReq): NetworkResponse<String>

    @GET("/api/user/v1/info")
    suspend fun getUserInfo(): NetworkResponse<UserDetailDto>

    @POST("/api/user/v1/update")
    suspend fun updateUserInfo(
        @Body req: UserUpdateRequest
    ): NetworkResponse<Unit>

    @POST("api/user/v1/check_in")
    suspend fun checkIn(): NetworkResponse<String>

    // 上报听歌时长(秒)
    @POST("api/user/v1/report_duration")
    suspend fun reportDuration(@Query("seconds") seconds: Int): NetworkResponse<Unit>

    @POST("/api/user/v1/VIP")
    suspend fun changeVIPLevel(@Body req: ChangeVIPLevelReq): NetworkResponse<Unit>

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

    @POST("/api/songs/v1/play/{id}")
    suspend fun playSong(@Path(value = "id") id: String): NetworkResponse<Unit>

    /*
    喜欢
     */
    // 获取喜欢列表
    @GET("/api/like/v1/listSong")
    suspend fun getLikedSong(): NetworkResponse<NetworkPageData<SongListItemDto>>
    @GET("/api/like/v1/listAlbum")
    suspend fun getLikedAlbum(): NetworkResponse<NetworkPageData<AlbumDto>>
    @GET("/api/like/v1/listPlaylist")
    suspend fun getLikedPlaylists(): NetworkResponse<NetworkPageData<PlaylistDto>>

    //喜欢接口
    @POST("/api/like/v1/like")
    suspend fun toggleLike(@Body req: LikeReq): NetworkResponse<Unit>

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

    @GET("/api/artist/v1/detail")
    suspend fun getArtistById(
        @Query("id") id: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): NetworkResponse<ArtistDetailResp>


    /*
    Album专辑
    */
    @GET("/api/album/v1/page")
    suspend fun getAlbums(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("artistId") artistId: String? = null,
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

    @GET("/api/relationship/v1/users")
    suspend fun getFollowedUsers(): NetworkResponse<NetworkPageData<UserDto>>

    @GET("/api/relationship/v1/artists")
    suspend fun getFollowedArtists(): NetworkResponse<NetworkPageData<ArtistDto>>

    @GET("/api/relationship/v1/fans")
    suspend fun getFans(): NetworkResponse<NetworkPageData<UserDto>>

    @GET("/api/relationship/v1/friends")
    suspend fun getFriends(): NetworkResponse<NetworkPageData<UserDto>>

    /*
    RankList排行榜
     */
    @GET("/api/rankList/v1/peeks")
    suspend fun getRankListPeeks(): NetworkResponse<List<RankListPeakDto>>

    @GET("/api/rankList/v1/detail")
    suspend fun rankListDetail(@Query("id") id: String): NetworkResponse<RankListDetailDto>

    /*
    Playlist (歌单)
     */
    @GET("/api/playlist/v1/my")
    suspend fun getMyPlaylists(): NetworkResponse<List<PlaylistDto>>

    @GET("/api/playlist/v1/public")
    suspend fun getPublicPlaylists(): NetworkResponse<NetworkPageData<PlaylistDto>>

    @GET("/api/playlist/v1/detail/{id}")
    suspend fun getPlaylistDetail(@Path("id") id: String): NetworkResponse<PlaylistDetailDto>

    @POST("/api/playlist/v1/song/add")
    suspend fun addSongToPlaylist(@Body req: PlaylistSongReq): NetworkResponse<Unit>

    @POST("/api/playlist/v1/song/remove")
    suspend fun removeSongFromPlaylist(@Body req: PlaylistSongReq): NetworkResponse<Unit>

    @POST("/api/playlist/v1/delete/{id}")
    suspend fun deletePlaylist(@Path("id") id: String): NetworkResponse<Unit>

    @Multipart
    @POST("/api/playlist/v1/add")
    suspend fun addPlaylist(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody?,
        @Part cover: MultipartBody.Part?
    ): NetworkResponse<Unit>

    @POST("/api/playlist/v1/changePublicStatus/{id}")
    suspend fun changePublicStatus(
        @Path("id") id: String,
    ): NetworkResponse<Unit>

    @Multipart
    @POST("/api/playlist/v1/update")
    suspend fun updatePlaylist(
        @Part("id") id: RequestBody,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody?,
        @Part cover: MultipartBody.Part?
    ): NetworkResponse<Unit>

    /*
    history历史
     */

    @GET("/api/history/v1/list")
    suspend fun getHistory(): NetworkResponse<List<SongListItemDto>>

    /*
    recommend推荐
     */
    @GET("api/v1/recommend/daily_songs")
    suspend fun getDailyRecommendSongs(): NetworkResponse<List<SongListItemDto>>

    @GET("api/v1/recommend")
    suspend fun getAlsoListening(): NetworkResponse<RecommendCardDto>

    /*
    Search搜索
     */
    @GET("api/search/v1")
    suspend fun searchComprehensive(
        @Query("keyword") keyword: String,
        @Query("type") type: String = "comprehensive"
    ): NetworkResponse<SearchComprehensiveDto>

    @GET("api/search/v1")
    suspend fun searchSongs(
        @Query("keyword") keyword: String,
        @Query("type") type: String = "song",
        @Query("page") page: Int,
        @Query("size") size: Int
    ): NetworkResponse<NetworkPageData<SongListItemDto>>

    @GET("api/search/v1")
    suspend fun searchPlaylists(
        @Query("keyword") keyword: String,
        @Query("type") type: String = "playlist",
        @Query("page") page: Int,
        @Query("size") size: Int
    ): NetworkResponse<NetworkPageData<PlaylistDto>>

    @GET("api/search/v1")
    suspend fun searchAlbums(
        @Query("keyword") keyword: String,
        @Query("type") type: String = "album",
        @Query("page") page: Int,
        @Query("size") size: Int
    ): NetworkResponse<NetworkPageData<AlbumDto>>

    @GET("api/search/v1")
    suspend fun searchArtists(
        @Query("keyword") keyword: String,
        @Query("type") type: String = "artist",
        @Query("page") page: Int,
        @Query("size") size: Int
    ): NetworkResponse<NetworkPageData<ArtistDto>>

    @GET("api/search/v1")
    suspend fun searchUsers(
        @Query("keyword") keyword: String,
        @Query("type") type: String = "user",
        @Query("page") page: Int,
        @Query("size") size: Int
    ): NetworkResponse<NetworkPageData<UserDetailDto>>


    @GET("/api/common/v1/check_update")
    suspend fun checkUpdate(
        @Query("versionCode") versionCode: Int
    ): NetworkResponse<AppUpdateDto>


    /*
    Chat 私信功能
     */
    @GET("/api/chat/v1/history")
    suspend fun getChatHistory(
        @Query("targetUserId") targetUserId: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): NetworkResponse<List<ChatMessage>>

    @GET("api/chat/v1/sessions")
    suspend fun getChatSessions(): NetworkResponse<List<ChatSessionDto>>

    /*
    通知
     */

    @POST("api/notify/v1/page")
    suspend fun getNotifyPage(@Body req: PageReq): NetworkResponse<NetworkPageData<NotifyDto>>

    @GET("api/notify/v1/unread_count")
    suspend fun getUnreadCount(): NetworkResponse<Map<String, Int>>


    /*
     * 评论相关
     */
    @POST("/api/comment/add")
    suspend fun addComment(@Body req: CommentAddReq): NetworkResponse<Unit>

    @DELETE("/api/comment/delete/{id}")
    suspend fun deleteComment(@Path("id") id: String): NetworkResponse<Unit>

    @POST("/api/comment/like/{id}")
    suspend fun likeComment(@Path("id") id: String): NetworkResponse<Int>

    @GET("/api/comment/list")
    suspend fun getComments(
        @Query("resourceType") resourceType: String,
        @Query("resourceId") resourceId: String,
        @Query("queryType") queryType: String = "all", // 默认为 "all"
        @Query("page") page: Int,
        @Query("size") size: Int
    ): NetworkResponse<NetworkPageData<CommentDto>>

    @GET("/api/comment/detail")
    suspend fun getCommentDetail(
        @Query("id") id: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): NetworkResponse<CommentDto>

    /*
    Feed动态
     */
    @GET("/api/feed/v1/list")
    suspend fun getFeeds(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): NetworkResponse<NetworkPageData<FeedItemDto>>

    @GET("/api/feed/v1/following")
    suspend fun getFollowingFeeds(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): NetworkResponse<NetworkPageData<FeedItemDto>>

    @POST("/api/feed/v1/publish")
    suspend fun publishFeed(@Body req: PublishFeedReq): NetworkResponse<String>


    /*
    大众
     */
    @Multipart
    @POST("/api/common/v1/file/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part?,
        @Part("flag") flag: RequestBody,
    ): NetworkResponse<String>

}
