package com.shin.vicmusic.core.network.datasource

import android.util.Log
import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.User
import com.shin.vicmusic.core.model.api.AlbumDto
import com.shin.vicmusic.core.model.api.SongDetailDto
import com.shin.vicmusic.core.model.api.SongListItemDto
import com.shin.vicmusic.core.model.request.AlbumPageReq
import com.shin.vicmusic.core.model.request.ArtistPageReq
import com.shin.vicmusic.core.model.request.FollowReq
import com.shin.vicmusic.core.model.request.LikeSongReq
import com.shin.vicmusic.core.model.request.SongPageReq
import com.shin.vicmusic.core.model.request.UserLoginReq
import com.shin.vicmusic.core.model.request.UserRegisterReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.model.response.NetworkResponse
import com.shin.vicmusic.core.network.retrofit.MyNetworkApiService
import retrofit2.HttpException
import retrofit2.http.Body
import retrofit2.http.Query
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

        val resp= try {
            call()
        } catch (e: IOException) {
            NetworkResponse(status = -1, message = "网络连接失败: ${e.message}", data = null)
        } catch (e: HttpException) {
            NetworkResponse(status = e.code(), message = "HTTP 错误: ${e.message()}", data = null)
        } catch (e: Exception) {
            NetworkResponse(status = -99, message = "未知错误: ${e.message}", data = null)
        }
        Log.d("HTTPPPPPPPP",resp.toString())
        return resp as NetworkResponse<T>
    }
    /*
    User用户
     */
    suspend fun mailCode(@Query(value = "to")to:String, @Query(value = "captcha")captcha:String):NetworkResponse<Unit>{
        return safeApiCall { service.mailCode(to, captcha) }
    }

    suspend fun login(req: UserLoginReq): NetworkResponse<String> {
        return safeApiCall { service.login(req) }
    }

    suspend fun register(@Body req: UserRegisterReq):NetworkResponse<User>{
        return safeApiCall { service.register(req) }
    }

    suspend fun userInfo(): NetworkResponse<User> {
        val dtoResponse = safeApiCall { service.userInfo() }
        if(dtoResponse.status == 0 && dtoResponse.data != null){
            val domainUser = dtoResponse.data.toDomain()
            return NetworkResponse(status = 0, message = dtoResponse.message, data = domainUser)
        }
        return NetworkResponse(status = dtoResponse.status, message = dtoResponse.message, data = null)
    }

    /*
    Song歌曲
     */
    suspend fun songs(req: SongPageReq = SongPageReq()): NetworkResponse<NetworkPageData<SongListItemDto>>{
        return safeApiCall { service.songs(
            page = req.page,
            size = req.size,
            artistId = req.artistId,
            albumId = req.albumId
        ) }
    }

    // 获取详情：DTO -> Domain
    suspend fun songDetail(@Query(value = "id") id:String,): NetworkResponse<SongDetailDto>{
       return safeApiCall { service.songDetail(id) }
    }


    /*
    Artist艺术家
     */
    suspend fun getArtists(
        artistPageReq: ArtistPageReq
    ): NetworkResponse<NetworkPageData<Artist>>{
        return safeApiCall { service.getArtists(
            page = artistPageReq.page,
            size = artistPageReq.size,
            region = artistPageReq.region,
            type = artistPageReq.type,
            style = artistPageReq.style
        ) }
    }

    suspend fun getArtistById(artistId: String): NetworkResponse<Artist>{
        return safeApiCall { service.getArtistById(artistId) }
    }

    /*
    Album专辑
     */
    suspend fun getAlbums(
        albumPageReq: AlbumPageReq
    ): NetworkResponse<NetworkPageData<AlbumDto>>{
        return safeApiCall { service.getAlbums(
            page = albumPageReq.page,
            size = albumPageReq.size
        ) }
    }

    suspend fun getAlbumById(id: String): NetworkResponse<AlbumDto> {
        return safeApiCall { service.getAlbumById(id) }
    }

    /*
    喜欢
    */
    suspend fun likedSongs(): NetworkResponse<NetworkPageData<SongListItemDto>>{
        return safeApiCall { service.getLikedSongs() }
    }

    // 喜欢歌曲
    suspend fun likeSong(likeSongReq: LikeSongReq): NetworkResponse<Unit>{
        return safeApiCall { service.likeSong(likeSongReq) }
    }

    /*
    用户关系
     */
    suspend fun follow(followReq: FollowReq): NetworkResponse<String>{
        return safeApiCall { service.follow(followReq) }
    }

}