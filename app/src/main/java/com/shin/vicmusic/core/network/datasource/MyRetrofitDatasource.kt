package com.shin.vicmusic.core.network.datasource

import android.util.Log
import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.User
import com.shin.vicmusic.core.model.request.LikeSongReq
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

    suspend fun mailCode(@Query(value = "to")to:String, @Query(value = "captcha")captcha:String):NetworkResponse<Unit>{
        return safeApiCall { service.mailCode(to, captcha) }
    }

    suspend fun login(req: UserLoginReq): NetworkResponse<String> {
        return safeApiCall { service.login(req) }
    }

    suspend fun register(@Body req: UserRegisterReq):NetworkResponse<User>{
        return safeApiCall { service.register(req) }
    }

    // [修改] 获取列表：DTO -> Domain
    suspend fun songs(): NetworkResponse<NetworkPageData<Song>>{
        val dtoResponse = safeApiCall { service.songs() }

        // 如果成功，进行数据转换
        if (dtoResponse.status == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            // 使用 Mapper 扩展函数转换
            val domainList = dtoList.map { it.toDomain() }

            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            return NetworkResponse(status = 0, message = dtoResponse.message, data = domainData)
        }

        // 失败则原样返回错误信息
        return NetworkResponse(status = dtoResponse.status, message = dtoResponse.message, data = null)
    }

    // [修改] 获取详情：DTO -> Domain
    suspend fun songDetail(@Query(value = "id") id:String,): NetworkResponse<Song>{
        val dtoResponse = safeApiCall { service.songDetail(id) }

        if (dtoResponse.status == 0 && dtoResponse.data != null) {
            return NetworkResponse(status = 0, message = dtoResponse.message, data = dtoResponse.data.toDomain())
        }
        return NetworkResponse(status = dtoResponse.status, message = dtoResponse.message, data = null)
    }

    // [新增] 对应 Service 的 userInfo 方法
    suspend fun userInfo(): NetworkResponse<User> {
        return safeApiCall { service.userInfo() }
    }

    // [修改] 获取喜欢列表：DTO -> Domain
    suspend fun likedSongs(): NetworkResponse<NetworkPageData<Song>>{
        val dtoResponse = safeApiCall { service.getLikedSongs() }

        if (dtoResponse.status == 0 && dtoResponse.data != null) {
            val dtoList = dtoResponse.data.list ?: emptyList()
            val domainList = dtoList.map { it.toDomain() }
            val domainData = NetworkPageData(
                list = domainList,
                pagination = dtoResponse.data.pagination
            )
            return NetworkResponse(status = 0, message = dtoResponse.message, data = domainData)
        }
        return NetworkResponse(status = dtoResponse.status, message = dtoResponse.message, data = null)
    }

    // [新增] 喜欢歌曲
    suspend fun likeSong(likeSongReq: LikeSongReq): NetworkResponse<Unit>{
        return safeApiCall { service.likeSong(likeSongReq) }
    }
}