package com.shin.vicmusic.feature.player

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.shin.vicmusic.core.model.Song
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
import javax.inject.Inject
import javax.inject.Singleton

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
 * 全局播放器 ViewModel。
 * 这是一个应用作用域的单例ViewModel，负责管理整个应用的 ExoPlayer 实例和播放状态。
 * 所有需要播放器功能的 Composable 都可以注入此 ViewModel 来控制和观察播放。
 */
@Singleton     // 保持 Singleton 以表明它是单例意图（虽然 HiltViewModel 生命周期由 ViewModelStoreOwner 决定，但在单 Activity 应用中 MainRoute 获取的实例实际上是全局的）
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context, // Hilt 注入应用上下文
    private val queueManager: PlaybackQueueManager
) : ViewModel() {

    private val TAG = "PlayerViewModel"

    // 应用程序中唯一的 ExoPlayer 实例
    private var exoPlayer: ExoPlayer? = null

    // 当前正在播放的歌曲
    private val _currentPlayingSong = MutableStateFlow<Song?>(null)
    val currentPlayingSong: StateFlow<Song?> = _currentPlayingSong.asStateFlow()

    // 播放队列中的所有歌曲（核心数据）
    val currentQueueIndex: StateFlow<Int> = queueManager.currentIndex
    // 供外部观察，用于显示在播放列表详情界面 (Playlist Detail UI)
    val playbackQueue: StateFlow<List<Song>> = queueManager.queue

    // 播放器状态
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    // 用于管理播放进度更新协程的 Job
    private var progressJob: Job? = null

    init {
        // ViewModel 初始化时，创建 ExoPlayer 实例。
        // 此实例将在整个应用生命周期中保持不变。
        exoPlayer = ExoPlayer.Builder(context).build()
        setupPlayerListener() // 设置 ExoPlayer 监听器以更新播放状态
        Log.d(TAG, "PlayerViewModel initialized. ExoPlayer instance created.")
    }

    /**
     * 播放指定的歌曲。
     * 如果当前有歌曲正在播放，将停止并播放新歌曲。
     * @param song 要播放的歌曲对象
     * @param queue 可选参数：如果提供，则设置一个新队列，并播放该歌曲。
     */
    fun playSong(song: Song, queue: List<Song>? = null) {
        val newQueue = queue ?: listOf(song)
        val startIndex = newQueue.indexOfFirst { it.id == song.id } // 找到歌曲在队列中的索引

        // 1. 更新播放队列状态
        queueManager.setQueue(newQueue, startIndex)
        _currentPlayingSong.value = queueManager.getCurrentSong()

        // 2. 更新 ExoPlayer 媒体项
        val mediaItems = newQueue.map { MediaItem.fromUri(ResourceUtil.r2(it.uri)) }

        exoPlayer?.apply {
            setMediaItems(mediaItems) // 设置整个队列
            seekTo(startIndex, 0)     // 跳转到正确的索引
            prepare()
            playWhenReady = true
        }
        _playerState.update { it.copy(isPlaying = true) }
        startProgressUpdate()
        Log.d(TAG, "播放歌曲: ${song.title} . Index为: $startIndex, 队列大小: ${newQueue.size}")
    }

    /**
     * 切换当前歌曲的播放/暂停状态。
     */
    fun togglePlayPause() {
        exoPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                stopProgressUpdate()
            } else {
                it.play()
                startProgressUpdate()
            }
            _playerState.update { state -> state.copy(isPlaying = it.isPlaying) } // 更新 isPlaying 状态
            Log.d(TAG, "歌曲播放状态切换为: ${it.isPlaying}")
        }
    }

    /**
     * 跳转到当前歌曲的指定位置。
     * @param positionMs 要跳转到的位置 (毫秒)
     */
    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        _playerState.update { it.copy(currentPosition = positionMs) } // 立即更新当前位置
        Log.d(TAG, "跳转到当前歌曲的: $positionMs ms")
    }

    /**
     * 跳到播放列表中的下一首歌曲。
     */
    fun skipToNext() {
        // 1. 计算下一个索引
        val nextIndex = queueManager.getNextIndex()
        if (nextIndex == -1) return

        // 2.更新 Manager 的索引
        queueManager.updateIndex(nextIndex)
        _currentPlayingSong.value = queueManager.getCurrentSong()

        // 3. 通知 ExoPlayer 跳转
        exoPlayer?.apply {
            seekToNextMediaItem()
            playWhenReady = true // 确保跳转后继续播放
        }
        Log.d(TAG,"跳转到下一首歌曲,索引为:$nextIndex")
    }

    /**
     * 跳到播放列表中的上一首歌曲
     */
    fun skipToPrevious() {
        val queue = playbackQueue.value // 使用公开的 flow 属性获取数据
        if (queue.isEmpty() || exoPlayer == null) {
            Log.w(TAG, "Queue is empty or player is null, cannot skip previous.")
            return
        }

        // ⭐ 1. 委托：计算上一个索引并更新 Manager 内部状态
        val prevIndex = queueManager.getPreviousIndex()
        queueManager.updateIndex(prevIndex) // 更新 Manager 的内部状态

        // 2. 通知 ExoPlayer 跳转 (命令)
        exoPlayer?.apply {
            seekToPreviousMediaItem()
            playWhenReady = true
        }
        Log.d(TAG,"跳转到上一首歌曲,索引为:$prevIndex")
    }

    /**
     * 启动一个协程循环，每隔 200ms 更新一次播放进度。
     * 这保证了 UI 进度条的流畅更新，同时避免了主线程阻塞。
     */
    private fun startProgressUpdate() {
        // 如果任务已存在，先取消旧任务，防止重复启动
        progressJob?.cancel()

        progressJob = viewModelScope.launch {
            // 确保协程在 ViewModel 生命周期内运行
            while (isActive && exoPlayer != null) {
                exoPlayer?.let { player ->
                    val currentPos = player.currentPosition
                    val totalDuration = player.duration
                    val bufferedPos = player.bufferedPosition

                    // 通过 StateFlow 更新 UI
                    _playerState.update { currentState ->
                        currentState.copy(
                            currentPosition = if (currentPos >= 0) currentPos else 0,
                            // duration 为 -9223372036854775807L (C.TIME_UNSET) 表示未知，需处理
                            duration = if (totalDuration > 0) totalDuration else 0,
                            bufferedPosition = bufferedPos
                        )
                    }
                }
                delay(200L) // 更新频率：每 200 毫秒
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

    /**
     * 设置 ExoPlayer 监听器，以便在播放状态变化时自动更新 ViewModel 内部状态。
     */
    private fun setupPlayerListener() {
        exoPlayer?.addListener(object : Player.Listener {
            // 1. 监听播放状态改变
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // 根据播放状态来启动/停止进度更新协程
                if (isPlaying) {
                    startProgressUpdate()
                } else {
                    stopProgressUpdate()
                }
                // 更新 StateFlow 中的 isPlaying 状态，供 UI 观察
                _playerState.update { it.copy(isPlaying = isPlaying) }
                Log.d(TAG, "ExoPlayer onIsPlayingChanged: $isPlaying")
            }

            // 2. 监听播放状态改变 (如：准备就绪、缓冲、播放结束)
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    Log.d(TAG, "ExoPlayer onPlaybackStateChanged: STATE_ENDED, attempting to skip next.")

                    //  1. 委托 Manager 获取下一索引
                    val nextIndex = queueManager.getNextIndex()
                    val currentQueueIndexValue = currentQueueIndex.value // 使用暴露的 Flow 获取当前索引
                    val currentQueueSize = playbackQueue.value.size

                    if (currentQueueSize > 0 && nextIndex != currentQueueIndexValue) {
                        // 如果队列非空且下一首不是当前这首（即需要继续播放）

                        //  2. 更新 Manager 状态
                        queueManager.updateIndex(nextIndex)

                        // 3. 跳转到计算出的下一首歌
                        exoPlayer?.apply {
                            seekTo(nextIndex, 0)
                            playWhenReady = true
                        }
                        Log.d(TAG, "STATE_ENDED: Forced seek to index $nextIndex (Next song).")

                    } else {
                        // 播放结束或队列为空，停止播放
                        _playerState.update { it.copy(isPlaying = false, currentPosition = 0) }
                        stopProgressUpdate()
                        Log.d(TAG, "STATE_ENDED: Playback stopped.")
                    }
                }
            }
        })
    }

    /**
     * 当 ViewModel 被销毁时调用，用于释放 ExoPlayer 资源。
     */
    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        exoPlayer = null
        stopProgressUpdate() // 停止任何正在进行的进度更新
        _playerState.update { PlayerState() } // 重置播放器状态
        _currentPlayingSong.value = null // 清空当前播放歌曲
        Log.d(TAG, "PlayerViewModel被销毁,ExoPlayer资源释放")
    }

    /**
     * 将一首歌曲添加到当前的播放队列末尾。
     * @param song 要添加的歌曲对象
     */
    fun addSongToQueue(song: Song) {
        queueManager.addSong(song)
        val mediaItem = MediaItem.fromUri(ResourceUtil.r2(song.uri))
        exoPlayer?.addMediaItem(mediaItem)
        Log.d(TAG,"添加歌曲,${song.title}")
    }

    /**
     * 播放队列中指定索引的歌曲。
     * @param index 歌曲在当前队列中的索引
     */
    fun playSongAtIndex(index: Int) {
        val queue = playbackQueue.value // 使用公开的 flow 属性获取数据
        if (index !in queue.indices || exoPlayer == null) {
            Log.e(TAG, "队列中不存在索引$index,或exoplayer为空")
            return
        }

        //  1.更新 Manager 内部状态
        queueManager.updateIndex(index)

        // 2. 通知 ExoPlayer 跳转
        exoPlayer?.apply {
            seekTo(index, 0) // 跳转到队列中的指定索引
            playWhenReady = true
        }

        // 3. 播放状态更新
        _playerState.update { it.copy(isPlaying = true) }
        startProgressUpdate()

        Log.d(TAG, "播放索引更新为:$index")
    }
}