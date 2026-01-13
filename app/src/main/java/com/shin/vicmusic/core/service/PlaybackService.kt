package com.shin.vicmusic.core.service

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@UnstableApi // 1. 全局声明使用 Unstable API
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var exoPlayer: ExoPlayer

    companion object {
        private var simpleCache: SimpleCache? = null

        @Synchronized
        fun getCache(context: Context): SimpleCache {
            if (simpleCache == null) {
                val cacheDir = File(context.cacheDir, "media_cache")
                val evictor = LeastRecentlyUsedCacheEvictor(1024 * 1024 * 512) // 512MB
                val dbProvider = StandaloneDatabaseProvider(context)
                simpleCache = SimpleCache(cacheDir, evictor, dbProvider)
            }
            return simpleCache!!
        }
    }

    override fun onCreate() {
        super.onCreate()

        // 2. [已修复] 纠正数据源工厂的配置
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(30_000, 1_200_000, 500, 0)
            .build()

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(getCache(this))
            .setUpstreamDataSourceFactory(httpDataSourceFactory) // 之前这里错了
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val mediaSourceFactory = DefaultMediaSourceFactory(this)
            .setDataSourceFactory(cacheDataSourceFactory)

        exoPlayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .build()

        mediaSession = MediaSession.Builder(this, exoPlayer).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}