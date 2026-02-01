package com.shin.vicmusic

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VicMusicApplication : Application(), androidx.work.Configuration.Provider {

    // 注入 Hilt 的 Worker 工厂，用于在 Worker 中支持依赖注入 (DI)
    @javax.inject.Inject
    lateinit var workerFactory: HiltWorkerFactory

    // 自定义 WorkManager 配置，使其使用 Hilt 的工厂来创建 Worker 实例
    override val workManagerConfiguration: androidx.work.Configuration
        get() = androidx.work.Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
    }
}
