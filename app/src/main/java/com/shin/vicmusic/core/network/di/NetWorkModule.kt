package com.shin.vicmusic.core.network.di

import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * 网络依赖注入模块
 */
object NetWorkModule {
    fun providesNetworkJson(): Json=Json{
        ignoreUnknownKeys=true
    }
    fun okHttpCallFactory(
        okHttpClient: OkHttpClient
    ): Call.Factory=okHttpClient

    fun providesOkHttpClient(): OkHttpClient{
        // 创建日志拦截器 (Create logging interceptor)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            // 设置日志级别为 BODY，这将打印请求和响应的全部信息，包括 header 和 body
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10,TimeUnit.SECONDS)
            .writeTimeout(10,TimeUnit.SECONDS)
            .readTimeout(10,TimeUnit.SECONDS)
            .build()
    }
}