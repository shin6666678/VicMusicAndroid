package com.shin.vicmusic.feature.player

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.feature.playList.PlaybackQueueManager
import com.shin.vicmusic.util.ResourceUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import com.shin.vicmusic.core.data.repository.PlayerRepository
import com.shin.vicmusic.core.domain.PayType
import com.shin.vicmusic.core.domain.usecase.CheckVipPermissionUseCase
import com.shin.vicmusic.feature.auth.AuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 播放器状态数据类。
 * 封装了播放器的当前状态，如是否正在播放、时长、当前位置等。
 */
data class PlayerState(
    val isPlaying: Boolean = false,         // 是否正在播放
    val duration: Long = 0,                 // 歌曲总时长 (毫秒)
    val currentPosition: Long = 0,          // 当前播放位置 (毫秒)
    val bufferedPosition: Long = 0,         // 缓冲位置 (毫秒)
    val currentLyricLineIndex: Int = -1,    // 当前高亮歌词行索引 (暂未实现，预留)
)

/**
 * 全局播放器 ViewModel。负责管理整个应用的 ExoPlayer 实例和播放状态。
 */
@Singleton
class PlayerManager @Inject constructor(
    @ApplicationContext private val context: Context, // Hilt 注入应用上下文
    private val queueManager: PlaybackQueueManager,
    private val playerRepository: PlayerRepository,          // [注入] 仓库
    private val checkVipPermission: CheckVipPermissionUseCase // [注入] UseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val TAG = "PlayerManager"
    private var exoPlayer: ExoPlayer? = null

    private val _currentPlayingSong = MutableStateFlow<Song?>(null)
    val currentPlayingSong: StateFlow<Song?> = _currentPlayingSong.asStateFlow()

    val currentQueueIndex: StateFlow<Int> = queueManager.currentIndex
    val playbackQueue: StateFlow<List<Song>> = queueManager.queue

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    // [新增] UI 事件流，用于通知 UI 层显示 Toast 或 Dialog
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    // 用于管理播放进度更新协程的 Job
    private var progressJob: Job? = null

    init {
        exoPlayer = ExoPlayer.Builder(context).build()
        setupPlayerListener()

        //恢复播放历史
        restoreLastPlayedSong()

        //监听当前歌曲变化并自动保存
        scope.launch {
            currentPlayingSong.collect { song ->
                playerRepository.saveLastPlayedSong(song)
            }
        }
        Log.d(TAG, "PlayerViewModel initialized.")
    }

    /**
     * [核心方法] 统一切歌逻辑
     * 包含：边界检查 -> VIP权限检查 -> 队列更新 -> Player跳转
     * @param index 目标索引
     * @param showToast 如果无权限，是否显示 Toast 提示 (自动连播时通常不弹窗)
     * @return 是否切换成功
     */
    private suspend fun performSwitchSong(index: Int, showToast: Boolean = true): Boolean {
        val queue = playbackQueue.value
        if (index !in queue.indices || exoPlayer == null) return false

        val targetSong = queue[index]

        // 1. 权限检查
        val isAllowed = checkVipPermission(targetSong)
        if (!isAllowed) {
            if (showToast) {
                _uiEvent.emit("VIP专享歌曲，请开通VIP(VIP Only)")
            }
            return false
        }

        // 2. 更新队列状态
        queueManager.updateIndex(index)
        _currentPlayingSong.value = queueManager.getCurrentSong()

        // 3. 操作播放器
        exoPlayer?.apply {
            seekTo(index, 0)
            playWhenReady = true
        }
        // 确保状态更新为播放中
        _playerState.update { it.copy(isPlaying = true) }
        startProgressUpdate()

        Log.d(TAG, "切歌成功: ${targetSong.title}, Index: $index")
        return true
    }


    /**
     * 播放指定的歌曲（通常用于点击列表中的某首歌开始播放新列表）
     */
    fun playSong(song: Song, queue: List<Song>? = null) {
        scope.launch {
            // 对于 playSong，因为可能涉及重置队列，所以单独处理权限逻辑
            val isAllowed = checkVipPermission(song)
            if (!isAllowed) {
                _uiEvent.emit("VIP专享歌曲，请开通VIP(VIP Only)")
                return@launch
            }

            val newQueue = queue ?: listOf(song)
            val startIndex = newQueue.indexOfFirst { it.id == song.id }

            // 1. 重置队列
            queueManager.setQueue(newQueue, startIndex)
            _currentPlayingSong.value = queueManager.getCurrentSong()

            // 2. 重置 Player MediaItems
            val mediaItems = newQueue.map { it.toMediaItem() }

            exoPlayer?.apply {
                setMediaItems(mediaItems)
                seekTo(startIndex, 0)
                prepare()
                playWhenReady = true
            }
            _playerState.update { it.copy(isPlaying = true) }
            startProgressUpdate()
            Log.d(TAG, "播放新队列: ${song.title}")
        }
    }

    /**
     * 播放当前队列中指定索引的歌曲
     */
    fun playSongAtIndex(index: Int) {
        scope.launch {
            performSwitchSong(index, showToast = true)
        }
    }

    /**
     * 下一首
     */
    fun skipToNext() {
        scope.launch {
            val nextIndex = queueManager.getNextIndex()
            if (nextIndex != -1) {
                performSwitchSong(nextIndex, showToast = true)
            }
        }
    }

    /**
     * 上一首
     */
    fun skipToPrevious() {
        scope.launch {
            // 如果队列为空直接返回
            if (playbackQueue.value.isEmpty()) return@launch

            val prevIndex = queueManager.getPreviousIndex()
            performSwitchSong(prevIndex, showToast = true)
        }
    }
    /**
     * 切换播放/暂停
     */
    fun togglePlayPause() {
        exoPlayer?.let {
            if (it.playbackState == Player.STATE_ENDED) {
                it.seekTo(0)
            }
            if (it.isPlaying) {
                it.pause()
                stopProgressUpdate()
            } else {
                it.play()
                startProgressUpdate()
            }
            _playerState.update { state -> state.copy(isPlaying = it.isPlaying) }
        }
    }

    /**
     * 进度条拖动
     */
    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        _playerState.update { it.copy(currentPosition = positionMs) }
    }
    /**
     * 移除歌曲
     */
    fun removeSong(index: Int) {
        val queue = playbackQueue.value
        if (index in queue.indices) {
            queueManager.removeSongAt(index)
            exoPlayer?.removeMediaItem(index)
            Log.d(TAG, "已移除索引: $index")
        }
    }

    /**
     * 释放资源
     */
    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        stopProgressUpdate()
        _playerState.update { PlayerState() }
        _currentPlayingSong.value = null
    }
    /**
     * 添加歌曲到队尾
     */
    fun addSongToQueue(song: Song) {
        queueManager.addSong(song)
        exoPlayer?.addMediaItem(song.toMediaItem())
    }

    // 内部: 恢复上次播放状态
    private fun restoreLastPlayedSong() {
        try {
            val song = playerRepository.getLastPlayedSong()
            if (song != null) {
                queueManager.setQueue(listOf(song), 0)
                _currentPlayingSong.value = song
                exoPlayer?.setMediaItem(song.toMediaItem())
                exoPlayer?.prepare()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // 内部: 进度更新协程
    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && exoPlayer != null) {
                exoPlayer?.let { player ->
                    // 只有在播放时才频繁更新
                    if (player.isPlaying) {
                        _playerState.update {
                            it.copy(
                                currentPosition = player.currentPosition.coerceAtLeast(0),
                                duration = player.duration.coerceAtLeast(0),
                                bufferedPosition = player.bufferedPosition
                            )
                        }
                    }
                }
                delay(200L)
            }
        }
    }

    /**
     * 停止进度条更新协程。
     */
    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }

    // 内部: Player 事件监听
    private fun setupPlayerListener() {
        exoPlayer?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) startProgressUpdate() else stopProgressUpdate()
                _playerState.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    scope.launch {
                        val nextIndex = queueManager.getNextIndex()
                        val currentIdx = currentQueueIndex.value
                        val queueSize = playbackQueue.value.size

                        if (queueSize > 0 && nextIndex != currentIdx) {
                            // 自动切歌：调用通用逻辑，但不显示Toast (showToast = false)
                            // 如果自动切歌遇到VIP歌曲且无权限，则停止播放
                            val success = performSwitchSong(nextIndex, showToast = false)
                            if (!success) {
                                _playerState.update { it.copy(isPlaying = false, currentPosition = 0) }
                                stopProgressUpdate()
                                Log.d(TAG, "自动切歌失败(无权限)，停止播放")
                            }
                        } else {
                            // 列表播放结束
                            _playerState.update { it.copy(isPlaying = false, currentPosition = 0) }
                            stopProgressUpdate()
                        }
                    }
                }
            }
        })
    }

}