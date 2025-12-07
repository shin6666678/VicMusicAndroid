package com.shin.vicmusic

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VicMusicApplication : Application() {
    // 这里可以放置任何需要在应用启动时初始化的逻辑
    override fun onCreate() {
        super.onCreate()
        // 例如：初始化日志库，崩溃报告工具等
    }
}
