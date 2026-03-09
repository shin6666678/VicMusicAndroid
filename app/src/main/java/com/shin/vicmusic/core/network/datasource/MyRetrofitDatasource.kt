package com.shin.vicmusic.core.network.datasource

import android.R.attr.description
import android.R.attr.name
import android.util.Log
import com.shin.vicmusic.core.domain.ChatMessage
import com.shin.vicmusic.core.domain.User
import com.shin.vicmusic.core.domain.UserInfo
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
import com.shin.vicmusic.core.model.api.SongDetailDto
import com.shin.vicmusic.core.model.api.SongListItemDto
import com.shin.vicmusic.core.model.api.UserDetailDto
import com.shin.vicmusic.core.model.request.AlbumDetailReq
import com.shin.vicmusic.core.model.request.AlbumPageReq
import com.shin.vicmusic.core.model.request.ArtistDetailReq
import com.shin.vicmusic.core.model.request.ArtistPageReq
import com.shin.vicmusic.core.model.request.ChangeVIPLevelReq
import com.shin.vicmusic.core.model.request.CommentAddReq
import com.shin.vicmusic.core.model.request.FollowReq
import com.shin.vicmusic.core.model.request.LikeReq
import com.shin.vicmusic.core.model.request.PageReq
import com.shin.vicmusic.core.model.request.PlaylistSongReq
import com.shin.vicmusic.core.model.request.PublishFeedReq
import com.shin.vicmusic.core.model.request.SongPageReq
import com.shin.vicmusic.core.model.request.UserLoginReq
import com.shin.vicmusic.core.model.request.UserRegisterReq
import com.shin.vicmusic.core.model.request.UserUpdateRequest
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.model.response.NetworkResponse
import com.shin.vicmusic.core.network.retrofit.MyNetworkApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.http.Body
import retrofit2.http.Query
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // 标记为单例，全局共享
class MyRetrofitDatasource @Inject constructor(
    val service: MyNetworkApiService // 直接注入 Hilt 提供的接口实例
) {

    /**
     * 统一处理网络请求的异常，并封装成 NetworkResponse 返回。
     * @param call 具体执行的网络请求 suspend lambda。
     * @return 封装了结果或错误信息的 NetworkResponse。
     */
    private suspend fun <T> safeApiCall(call: suspend () -> NetworkResponse<T>): NetworkResponse<T> {

        val resp = try {
            call()
        } catch (e: IOException) {
            NetworkResponse(code = -1, message = "网络连接失败: ${e.message}", data = null)
        } catch (e: HttpException) {
            NetworkResponse(code = e.code(), message = "HTTP 错误: ${e.message()}", data = null)
        } catch (e: Exception) {
            NetworkResponse(code = -99, message = "未知错误: ${e.message}", data = null)
        }
        Log.d("HTTPPPPPPPP", resp.toString())
        return resp as NetworkResponse<T>
    }

    /*
    User用户
     */
    suspend fun mailCode(
        @Query(value = "to") to: String,
        @Query(value = "captcha") captcha: String
    ): NetworkResponse<Unit> {
        return safeApiCall { service.mailCode(to, captcha) }
    }

    suspend fun login(req: UserLoginReq): NetworkResponse<String> {
        return safeApiCall { service.login(req) }
    }

    suspend fun register(@Body req: UserRegisterReq): NetworkResponse<User> {
        return safeApiCall { service.register(req) }
    }

    suspend fun getUserInfo(): NetworkResponse<UserDetailDto> {
        return safeApiCall { service.getUserInfo() }
    }

    suspend fun getUserInfoById(userId: String): NetworkResponse<UserDetailDto> {
        return safeApiCall { service.getUserInfoById(userId) }
    }
    suspend fun updateUserInfo(
        name: String?=null,
        slogan: String?=null,
        sex: Int?=null,
        headImg: String?=null,
        bgImg:String?=null
    ): NetworkResponse<Unit> {
        return safeApiCall {
            service.updateUserInfo(
                UserUpdateRequest(
                    name=name,
                    slogan=slogan,
                    sex=sex,
                    headImg=headImg,
                    bgImg = bgImg
                )
            )
        }
    }

    suspend fun checkIn(): NetworkResponse<String> {
        return safeApiCall { service.checkIn() }
    }

    suspend fun reportDuration(seconds: Int): NetworkResponse<Unit> {
        return safeApiCall { service.reportDuration(seconds) }
    }

    suspend fun changeVIPLevel(req: ChangeVIPLevelReq = ChangeVIPLevelReq()): NetworkResponse<Unit> {
        return safeApiCall { service.changeVIPLevel(req) }
    }

    /*
    Song歌曲
     */
    suspend fun songs(req: SongPageReq = SongPageReq()): NetworkResponse<NetworkPageData<SongListItemDto>> {
        return safeApiCall {
            service.songs(
                page = req.page,
                size = req.size,
                artistId = req.artistId,
                albumId = req.albumId
            )
        }
    }

    // 获取详情：DTO -> Domain
    suspend fun songDetail(@Query(value = "id") id: String): NetworkResponse<SongDetailDto> {
        return safeApiCall { service.songDetail(id) }
    }

    suspend fun playSong(id: String): NetworkResponse<Unit> {
        return safeApiCall { service.playSong(id) }
    }


    /*
    Artist艺术家
     */
    suspend fun getArtists(
        artistPageReq: ArtistPageReq
    ): NetworkResponse<NetworkPageData<ArtistDto>> {
        return safeApiCall {
            service.getArtists(
                page = artistPageReq.page,
                size = artistPageReq.size,
                region = artistPageReq.region,
                type = artistPageReq.type,
                style = artistPageReq.style
            )
        }
    }

    suspend fun getArtistById(artistDetailReq: ArtistDetailReq): NetworkResponse<ArtistDetailResp> {
        return safeApiCall {
            service.getArtistById(
                id = artistDetailReq.id,
                page = artistDetailReq.page,
                size = artistDetailReq.size
            )
        }
    }

    /*
    Album专辑
     */
    suspend fun getAlbums(
        albumPageReq: AlbumPageReq
    ): NetworkResponse<NetworkPageData<AlbumDto>> {
        return safeApiCall {
            service.getAlbums(
                page = albumPageReq.page,
                size = albumPageReq.size,
                artistId = albumPageReq.artistId
            )
        }
    }

    suspend fun getAlbumById(albumDetailReq: AlbumDetailReq): NetworkResponse<AlbumDetailResp> {
        return safeApiCall {
            service.getAlbumById(
                id = albumDetailReq.id,
                page = albumDetailReq.page,
                size = albumDetailReq.size
            )
        }
    }

    /*
    喜欢
    */
    suspend fun likedSongs(): NetworkResponse<NetworkPageData<SongListItemDto>> {
        return safeApiCall { service.getLikedSong() }
    }
    suspend fun likedAlbums(): NetworkResponse<NetworkPageData<AlbumDto>> {
        return safeApiCall { service.getLikedAlbum() }
    }
    suspend fun likedPlaylists(): NetworkResponse<NetworkPageData<PlaylistDto>> {
        return safeApiCall { service.getLikedPlaylists() }
    }
    suspend fun toggleLike(likeSongReq: LikeReq): NetworkResponse<Unit> {
        return safeApiCall { service.toggleLike(likeSongReq) }
    }

    /*
    社交关系
     */
    suspend fun follow(followReq: FollowReq): NetworkResponse<Unit> {
        return safeApiCall { service.follow(followReq) }
    }

    suspend fun getFollowedUsers() = service.getFollowedUsers()

    suspend fun getFollowedArtists() = service.getFollowedArtists()

    suspend fun getFans() = service.getFans()

    suspend fun getFriends() = service.getFriends()


    /*
    RankList排行榜
     */
    suspend fun getRankListPeeks(): NetworkResponse<List<RankListPeakDto>> {
        return safeApiCall { service.getRankListPeeks() }
    }

    suspend fun rankListDetail(id: String): NetworkResponse<RankListDetailDto> {
        return safeApiCall { service.rankListDetail(id) }
    }


    /*
    Playlist 歌单
     */
    suspend fun getMyPlaylists(): NetworkResponse<List<PlaylistDto>> {
        return safeApiCall { service.getMyPlaylists() }
    }

    suspend fun getPublicPlaylists(): NetworkResponse<NetworkPageData<PlaylistDto>> {
        return safeApiCall { service.getPublicPlaylists() }
    }

    suspend fun getPlaylistDetail(id: String): NetworkResponse<PlaylistDetailDto> {
        return safeApiCall { service.getPlaylistDetail(id) }
    }

    suspend fun addSongToPlaylist(playlistId: String, songId: String): NetworkResponse<Unit> {
        return safeApiCall { service.addSongToPlaylist(PlaylistSongReq(playlistId, songId)) }
    }

    suspend fun removeSongFromPlaylist(playlistId: String, songId: String): NetworkResponse<Unit> {
        return safeApiCall { service.removeSongFromPlaylist(PlaylistSongReq(playlistId, songId)) }
    }

    suspend fun deletePlaylist(id: String): NetworkResponse<Unit> {
        return safeApiCall { service.deletePlaylist(id) }
    }

    suspend fun addPlaylist(
        name: String,
        description: String?,
        cover: File?
    ): NetworkResponse<Unit> {
        val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val descBody = description?.toRequestBody("text/plain".toMediaTypeOrNull())
        val coverPart = cover?.let {
            val body = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("cover", it.name, body)
        }
        return safeApiCall { service.addPlaylist(nameBody, descBody, coverPart) }
    }

    suspend fun updatePlaylist(
        id: String,
        name: String,
        description: String?,
        cover: File?
    ): NetworkResponse<Unit> {
        val idBody = id.toRequestBody("text/plain".toMediaTypeOrNull())
        val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val descBody = description?.toRequestBody("text/plain".toMediaTypeOrNull())
        val coverPart = cover?.let {
            val body = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("cover", it.name, body)
        }
        return safeApiCall { service.updatePlaylist(idBody, nameBody, descBody, coverPart) }
    }

    suspend fun changePublicStatus(id: String): NetworkResponse<Unit> {
        return safeApiCall { service.changePublicStatus(id) }
    }

    /*
    history历史记录
     */
    suspend fun getHistory() = safeApiCall { service.getHistory() }

    suspend fun getRecentPlayCount() =safeApiCall { service.getHistoryCount() }

    /*
    Recommend推荐
     */
    suspend fun getDailyRecommendSongs(): NetworkResponse<List<SongListItemDto>> {
        return service.getDailyRecommendSongs()
    }

    suspend fun getAlsoListening(): NetworkResponse<RecommendCardDto> {
        return safeApiCall { service.getAlsoListening() }
    }

    suspend fun checkUpdate(currentCode: Int): NetworkResponse<AppUpdateDto> {
        return safeApiCall { service.checkUpdate(currentCode) }
    }
    /*
Notify通知
 */
    suspend fun getNotifyPage(req: PageReq): NetworkResponse<NetworkPageData<NotifyDto>> {
        return safeApiCall { service.getNotifyPage(req) }
    }
    suspend fun getFeedNotifyPage(pageReq: PageReq) : NetworkResponse<NetworkPageData<NotifyDto>>{
        return safeApiCall { service.getFeedNotifyPage(pageReq) }
    }

    suspend fun getUnreadCount(): NetworkResponse<Map<String, Int>> {
        return safeApiCall { service.getUnreadCount() }
    }

    suspend fun getChatHistory(targetUserId: String,
                               page: Int,
                               size: Int):NetworkResponse<List<ChatMessage>>{
        return safeApiCall { service.getChatHistory(targetUserId, page, size) }
    }
    suspend fun getChatSessions(): NetworkResponse<List<ChatSessionDto>> {
        return safeApiCall { service.getChatSessions() }
    }

    /*
Comment评论
 */
    suspend fun addComment(req: CommentAddReq): NetworkResponse<Unit> {
        return safeApiCall { service.addComment(req) }
    }

    suspend fun deleteComment(id: String): NetworkResponse<Unit> {
        return safeApiCall { service.deleteComment(id) }
    }

    suspend fun likeComment(id: String): NetworkResponse<Int> {
        return safeApiCall { service.likeComment(id) }
    }

    suspend fun getComments(
        resourceType: String,
        resourceId: String,
        queryType: String = "all",
        page: Int,
        size: Int
    ): NetworkResponse<NetworkPageData<CommentDto>> {
        return safeApiCall {
            service.getComments(resourceType, resourceId, queryType, page, size)
        }
    }

    suspend fun getCommentDetail(id: String, page: Int, size: Int): NetworkResponse<CommentDto> {
        return safeApiCall { service.getCommentDetail(id,page,size) }
    }

    /*
Feed动态
 */
    suspend fun getFeeds(
        page: Int,
        size: Int
    ): NetworkResponse<NetworkPageData<FeedItemDto>> {
        return safeApiCall { service.getFeeds(page, size) }
    }

    suspend fun getFollowingFeeds(
        page: Int,
        size: Int
    ): NetworkResponse<NetworkPageData<FeedItemDto>> {
        return safeApiCall { service.getFollowingFeeds(page, size) }
    }

    suspend fun publishFeed(req: PublishFeedReq): NetworkResponse<String> {
        return safeApiCall { service.publishFeed(req) }
    }
    suspend fun likeFeed(feedId: String) : NetworkResponse<Unit>{
        return safeApiCall { service.likeFeed(feedId) }
    }

    suspend fun getUserFeeds(
        userId: String,
        page: Int,
        size: Int
    ): NetworkResponse<NetworkPageData<FeedItemDto>> {
        return safeApiCall { service.getUserFeeds(userId, page, size) }
    }

    /*
    大众
     */
    suspend fun uploadImage(
        file: File?,
        flag: String
    ): NetworkResponse<String> {
        val flagBody = flag.toRequestBody("text/plain".toMediaTypeOrNull())
        val filePart = file?.let {
            val body = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", it.name, body)
        }
        return safeApiCall { service.uploadImage(filePart,flagBody) }
    }
}