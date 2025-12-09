package com.shin.vicmusic.core.network.datasource

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.shin.vicmusic.core.config.Config
import com.shin.vicmusic.core.model.Song
import com.shin.vicmusic.core.model.User
import com.shin.vicmusic.core.model.request.UserLoginReq
import com.shin.vicmusic.core.model.request.UserRegisterReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.model.response.NetworkResponse
import com.shin.vicmusic.core.network.di.NetWorkModule
import com.shin.vicmusic.core.network.retrofit.MyNetworkApiService
import com.shin.vicmusic.feature.auth.AuthViewModel
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    suspend fun mailCode(@Query(value = "to")to:String,@Query(value = "captcha")captcha:String):NetworkResponse<Unit>{
        return safeApiCall { service.mailCode(to, captcha) }
    }

    suspend fun login(req: UserLoginReq): NetworkResponse<String> {
        return safeApiCall { service.login(req) }
    }

    suspend fun register(@Body req: UserRegisterReq):NetworkResponse<User>{
        return safeApiCall { service.register(req) }
    }
    suspend fun songs(): NetworkResponse<NetworkPageData<Song>>{
        return safeApiCall { service.songs() }
    }


    suspend fun songDetail(@Query(value = "id") id:String,): NetworkResponse<Song>{
        return safeApiCall { service.songDetail(id) }
    }

    // [新增] 获取喜欢歌曲列表方法
    suspend fun getLikedSongs(): NetworkResponse<NetworkPageData<Song>>{
        return safeApiCall { service.getLikedSongs() }
    }
}
