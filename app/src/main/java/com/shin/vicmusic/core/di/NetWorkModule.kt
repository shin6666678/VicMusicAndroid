package com.shin.vicmusic.core.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.shin.vicmusic.core.config.AppGlobalData
import com.shin.vicmusic.core.config.Config
import com.shin.vicmusic.core.network.retrofit.MyNetworkApiService
import com.shin.vicmusic.BuildConfig
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

@Module
@InstallIn(SingletonComponent::class)
object NetWorkModule {

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val authInterceptor = Interceptor { chain ->
            val builder = chain.request().newBuilder()
            AppGlobalData.token?.let { token ->
                // 检查你的后端是否需要 "Bearer " 前缀，大部分规范是需要的
                val headerValue = if (token.startsWith("Bearer")) token else "Bearer $token"
                builder.addHeader("Authorization", headerValue)
            }
            chain.proceed(builder.build())
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            // 添加专门处理 401 刷新的 Authenticator(认证器)
            // .authenticator(MyTokenAuthenticator())
            .connectTimeout(15, TimeUnit.SECONDS) // 建议稍微加长一点容错时间
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
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
            .baseUrl(Config.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(networkJson.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideMyNetworkApiService(retrofit: Retrofit): MyNetworkApiService {
        return retrofit.create(MyNetworkApiService::class.java)
    }
}