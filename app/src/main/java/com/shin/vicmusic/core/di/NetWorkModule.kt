package com.shin.vicmusic.core.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.shin.vicmusic.core.config.AppGlobalData
import com.shin.vicmusic.core.config.Config
import com.shin.vicmusic.core.network.retrofit.MyNetworkApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 网络依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class) // 1. 指定这是一个全局单例组件
object NetWorkModule {

    @Provides
    @Singleton // 2. 标记提供 Json 的方法
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient {
        // 创建日志拦截器 (Create logging interceptor)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            // 设置日志级别为 BODY，这将打印请求和响应的全部信息，包括 header 和 body
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        // [New] 添加认证拦截器
        val authInterceptor = Interceptor { chain ->
            val builder = chain.request().newBuilder()
            AppGlobalData.token?.let { token ->
                // 通常是 "Bearer $token"，请根据后端要求调整
                builder.addHeader("Authorization", token)
            }
            chain.proceed(builder.build())
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor) // [New] 注册拦截器
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        networkJson: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(Config.BASE_URL) // 4. 使用 Config 中的 BASE_URL
            .client(okHttpClient)
            .addConverterFactory(networkJson.asConverterFactory(contentType)) // 5. 绑定 Serialization
            .build()
    }

    @Provides
    @Singleton // 6. 最重要的一步：告诉 Hilt 如何提供 MyNetworkApiService
    fun provideMyNetworkApiService(retrofit: Retrofit): MyNetworkApiService {
        return retrofit.create(MyNetworkApiService::class.java)
    }
}