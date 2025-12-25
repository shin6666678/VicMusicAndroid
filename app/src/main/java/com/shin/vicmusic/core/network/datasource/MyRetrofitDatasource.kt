package com.shin.vicmusic.core.network.datasource

import android.util.Log
import com.shin.vicmusic.core.domain.User
import com.shin.vicmusic.core.model.api.AlbumDetailResp
import com.shin.vicmusic.core.model.api.AlbumDto
import com.shin.vicmusic.core.model.api.ArtistDto
import com.shin.vicmusic.core.model.api.PlaylistDetailDto
import com.shin.vicmusic.core.model.api.PlaylistDto
import com.shin.vicmusic.core.model.api.RankListDetailDto
import com.shin.vicmusic.core.model.api.RankListPeakDto
import com.shin.vicmusic.core.model.api.SongDetailDto
import com.shin.vicmusic.core.model.api.SongListItemDto
import com.shin.vicmusic.core.model.api.UserInfoDto
import com.shin.vicmusic.core.model.request.AlbumDetailReq
import com.shin.vicmusic.core.model.request.AlbumPageReq
import com.shin.vicmusic.core.model.request.ArtistPageReq
import com.shin.vicmusic.core.model.request.FollowReq
import com.shin.vicmusic.core.model.request.LikeSongReq
import com.shin.vicmusic.core.model.request.PlaylistSongReq
import com.shin.vicmusic.core.model.request.SongPageReq
import com.shin.vicmusic.core.model.request.UserLoginReq
import com.shin.vicmusic.core.model.request.UserRegisterReq
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
    private val service: MyNetworkApiService // 直接注入 Hilt 提供的接口实例
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
            NetworkResponse(status = -1, message = "网络连接失败: ${e.message}", data = null)
        } catch (e: HttpException) {
            NetworkResponse(status = e.code(), message = "HTTP 错误: ${e.message()}", data = null)
        } catch (e: Exception) {
            NetworkResponse(status = -99, message = "未知错误: ${e.message}", data = null)
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

    suspend fun userInfo(): NetworkResponse<UserInfoDto> {
        return safeApiCall { service.userInfo() }
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

    suspend fun getArtistById(artistId: String): NetworkResponse<ArtistDto> {
        return safeApiCall { service.getArtistById(artistId) }
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
        return safeApiCall { service.getLikedSongs() }
    }

    // 喜欢歌曲
    suspend fun likeSong(likeSongReq: LikeSongReq): NetworkResponse<Unit> {
        return safeApiCall { service.likeSong(likeSongReq) }
    }

    /*
    用户关系
     */
    suspend fun follow(followReq: FollowReq): NetworkResponse<Unit> {
        return safeApiCall { service.follow(followReq) }
    }


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

    suspend fun addPlaylist(name: String, description: String?, cover: File?): NetworkResponse<Unit> {
        val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val descBody = description?.toRequestBody("text/plain".toMediaTypeOrNull())
        val coverPart = cover?.let {
            val body = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("cover", it.name, body)
        }
        return safeApiCall { service.addPlaylist(nameBody, descBody, coverPart) }
    }

    suspend fun updatePlaylist(id: String, name: String, description: String?, cover: File?): NetworkResponse<Unit> {
        val idBody = id.toRequestBody("text/plain".toMediaTypeOrNull())
        val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val descBody = description?.toRequestBody("text/plain".toMediaTypeOrNull())
        val coverPart = cover?.let {
            val body = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("cover", it.name, body)
        }
        return safeApiCall { service.updatePlaylist(idBody, nameBody, descBody, coverPart) }
    }
}