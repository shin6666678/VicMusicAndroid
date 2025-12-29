package com.shin.vicmusic.core.manager

import android.content.Context
import android.util.Log
import androidx.media3.common.Player
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.shin.vicmusic.core.data.repository.PlayerRepository
import com.shin.vicmusic.core.data.repository.SongRepository
import com.shin.vicmusic.core.data.repository.UserRepository
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.domain.usecase.CheckVipPermissionUseCase
import com.shin.vicmusic.util.LrcHelper
import com.shin.vicmusic.util.ResourceUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

data class PlayerState(
    val isPlaying: Boolean = false,
    val duration: Long = 0,
    val currentPosition: Long = 0,
    val bufferedPosition: Long = 0,
    val currentLyricLineIndex: Int = -1,
)

@Singleton
class PlayerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val queueManager: PlaybackQueueManager,
    private val playerRepository: PlayerRepository,
    private val checkVipPermission: CheckVipPermissionUseCase,
    private val songRepository: SongRepository,
    private val userRepository: UserRepository
) {
    private val TAG = "PlayerManager"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        private var simpleCache: SimpleCache? = null

        @Synchronized
        fun getCache(context: Context): SimpleCache {
            if (simpleCache == null) {
                val cacheDir = File(context.cacheDir, "media_cache")
                val evictor = LeastRecentlyUsedCacheEvictor(1024 * 1024 * 512)
                val dbProvider = StandaloneDatabaseProvider(context)
                simpleCache = SimpleCache(cacheDir, evictor, dbProvider)
            }
            return simpleCache!!
        }
    }

    // [核心修复1] 调整 LoadControl
    // 将 maxBufferMs 设为 20分钟 (1,200,000ms)，确保能缓存整首歌曲，不再中途停止
    private val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            30_000,
            1_200_000, // maxBufferMs: 20分钟，允许 ExoPlayer 一口气下载整首歌
            500,
            0
        )
        .setBackBuffer(30_000, true)
        .setPrioritizeTimeOverSizeThresholds(true)
        .build()

    private fun buildExoPlayer(): ExoPlayer {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(getCache(context))
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(cacheDataSourceFactory)

        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .build()
    }

    private var exoPlayer: ExoPlayer = buildExoPlayer()

    private val _currentPlayingSong = MutableStateFlow<Song?>(null)
    val currentPlayingSong: StateFlow<Song?> = _currentPlayingSong.asStateFlow()

    val currentQueueIndex: StateFlow<Int> = queueManager.currentIndex
    val playbackQueue: StateFlow<List<Song>> = queueManager.queue

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    private var progressJob: Job? = null
    private var reportPlayJob: Job? = null
    private var reportTimeJob: Job? = null

    private val REPORT_INTERVAL = 60000L
    private val REPORT_SECONDS = 60

    private var isSeeking = false

    init {
        setupPlayerListener()
        restoreSession()

        scope.launch {
            currentPlayingSong.collect { song ->
                playerRepository.saveLastPlayedSong(song)
            }
        }
    }

    private suspend fun tryPlaySong(song: Song?, performPlay: () -> Unit) {
        if (song == null) return
        if (!checkVipPermission(song)) {
            _uiEvent.emit("VIP专享歌曲，请开通VIP(VIP Only)")
            return
        }

        _currentPlayingSong.value = song
        loadLyric(song)
        performPlay()
        playerRepository.saveLastPlayedSong(song)

        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        startReportPlayJob(song = song)
    }

    fun playSong(song: Song, queue: List<Song>? = null) {
        scope.launch {
            val newQueue = queue ?: listOf(song)
            val index = newQueue.indexOfFirst { it.id == song.id }
            queueManager.setQueue(newQueue, index)
            tryPlaySong(song) {
                exoPlayer.setMediaItems(newQueue.map { it.toMediaItem() })
                exoPlayer.seekTo(index, 0)
            }
        }
    }

    fun playAtIndex(index: Int) {
        scope.launch {
            val queue = playbackQueue.value
            if (index in queue.indices) {
                queueManager.updateIndex(index)
                val song = queue[index]
                tryPlaySong(song) {
                    exoPlayer.seekTo(index, 0)
                }
            }
        }
    }

    fun skipToNext() = playAtIndex(queueManager.getNextIndex())
    fun skipToPrevious() = playAtIndex(queueManager.getPreviousIndex())

    fun togglePlayPause() {
        if (exoPlayer.playbackState == Player.STATE_ENDED) exoPlayer.seekTo(0)
        if (exoPlayer.playWhenReady) exoPlayer.pause() else exoPlayer.play()
    }

    fun seekTo(pos: Long) {
        isSeeking = true
        exoPlayer.seekTo(pos)
        _playerState.update { it.copy(currentPosition = pos) }
    }

    fun removeSong(index: Int) {
        if (index in playbackQueue.value.indices) {
            queueManager.removeSongAt(index)
            exoPlayer.removeMediaItem(index)
        }
    }

    fun addSongToQueue(song: Song) {
        queueManager.addSong(song)
        exoPlayer.addMediaItem(song.toMediaItem())
    }

    private fun setupPlayerListener() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                updatePlayerState()
            }

            // [核心修复2] 监听加载状态
            override fun onIsLoadingChanged(isLoading: Boolean) {
                // 当 loading 状态改变时 (开始/停止缓冲)，检查是否需要启动定时器
                toggleProgressUpdate()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // 播放状态改变时，检查是否需要启动定时器
                toggleProgressUpdate()

                if (isPlaying) startReportTimeJob() else stopReportingDuration()
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) skipToNext()
                if (state == Player.STATE_READY) {
                    isSeeking = false
                    updatePlayerState()
                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                    isSeeking = false
                    updatePlayerState()
                }
            }
        })
    }

    // [核心修复3] 统一管理定时器启动逻辑
    private fun toggleProgressUpdate() {
        // 只要是在播放 OR 正在后台加载/缓冲，就保持 UI 更新
        // 这样即使暂停了，只要还在下载缓存，进度条也会走
        val shouldUpdate = exoPlayer.isPlaying || exoPlayer.isLoading
        if (shouldUpdate) {
            startProgressUpdate()
        } else {
            stopProgressUpdate()
        }
    }

    private fun updatePlayerState() {
        val currentPos = exoPlayer.currentPosition
        val lines = currentPlayingSong.value?.lyricList ?: emptyList()

        val isUiPlaying = exoPlayer.isPlaying ||
                (exoPlayer.playbackState == Player.STATE_BUFFERING && exoPlayer.playWhenReady)

        _playerState.update {
            it.copy(
                isPlaying = isUiPlaying,
                duration = exoPlayer.duration.coerceAtLeast(0),
                bufferedPosition = exoPlayer.bufferedPosition,
                currentPosition = if (isSeeking) it.currentPosition else currentPos,
                currentLyricLineIndex = lines.indexOfLast { line -> line.time <= currentPos }
            )
        }
    }

    private fun startProgressUpdate() {
        // 避免重复启动 job
        if (progressJob?.isActive == true) return

        progressJob = scope.launch {
            while (isActive) {
                // 只要不是正在拖拽，就更新。
                // 这里移除了 isPlaying 的判断，改为由 toggleProgressUpdate 外部控制协程的存活
                if (!isSeeking) {
                    updatePlayerState()
                }
                delay(200)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        // 停止时哪怕不更新也没关系，最后一次 updatePlayerState 会保留状态
        updatePlayerState()
    }

    private fun startReportPlayJob(song: Song) {
        reportPlayJob?.cancel()
        reportPlayJob = scope.launch(Dispatchers.IO) {
            try {
                delay(10000)
                songRepository.playSong(song.id)
                Log.d("PlayerManager", "Valid play recorded: ${song.title}")
            } catch (e: Exception) {
                if (e !is CancellationException) e.printStackTrace()
            }
        }
    }

    private fun startReportTimeJob() {
        stopReportingDuration()
        reportTimeJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(REPORT_INTERVAL)
                try {
                    userRepository.reportDuration(REPORT_SECONDS)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun stopReportingDuration() {
        reportTimeJob?.cancel()
        reportTimeJob = null
    }

    private fun restoreSession() {
        scope.launch {
            playerRepository.getLastPlayedSong()?.let { song ->
                queueManager.setQueue(listOf(song), 0)
                _currentPlayingSong.value = song
                Log.d("PlayerManager", "Restored: ${song.title}")
                loadLyric(song)
                exoPlayer.setMediaItem(song.toMediaItem())
                exoPlayer.prepare()
            }
        }
    }

    private fun loadLyric(song: Song) {
        if (song.lyric.isEmpty() || song.lyric == "") return
        scope.launch(Dispatchers.IO) {
            try {
                val text = java.net.URL(ResourceUtil.r2(song.lyric)).readText()
                val list = LrcHelper.parse(text)
                _currentPlayingSong.update {
                    if (it?.id == song.id) it.copy(lyricList = list) else it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun release() {
        exoPlayer.release()
        progressJob?.cancel()
        reportPlayJob?.cancel()
        reportTimeJob?.cancel()
    }
}