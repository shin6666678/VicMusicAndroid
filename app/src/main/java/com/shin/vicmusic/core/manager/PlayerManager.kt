package com.shin.vicmusic.core.manager

import android.content.Context
import android.util.Log
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.shin.vicmusic.core.data.repository.PlayerRepository
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
    private var exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

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
        setupPlayerListener()
        restoreSession()

        //监听当前歌曲变化并自动保存
        scope.launch {
            currentPlayingSong.collect { song ->
                playerRepository.saveLastPlayedSong(song)
            }
        }
    }



    // 核心播放入口：处理权限与播放
    private suspend fun tryPlaySong(song: Song?, performPlay: () -> Unit) {
        if (song == null) return
        if (!checkVipPermission(song)) {
            _uiEvent.emit("VIP专享歌曲，请开通VIP(VIP Only)")
            return
        }

        //更新当前歌曲状态，否则UI不刷新
        _currentPlayingSong.value = song
        Log.d("Mana111",song.toString())
        loadLyric(song)
        performPlay()
        playerRepository.saveLastPlayedSong(song)

        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        _playerState.update { it.copy(isPlaying = true) }
        startProgressUpdate()
    }
    /**
     * 播放指定的歌曲（通常用于点击列表中的某首歌开始播放新列表）
     */
    fun playSong(song: Song, queue: List<Song>? = null) {
        scope.launch {
            val newQueue = queue ?: listOf(song)
            val index = newQueue.indexOfFirst { it.id == song.id }

            // [修复] 必须设置队列数据
            queueManager.setQueue(newQueue, index)

            tryPlaySong(song) {
                exoPlayer.setMediaItems(newQueue.map { it.toMediaItem() })
                exoPlayer.seekTo(index, 0)
            }
        }
    }

    /**
     * 播放当前队列中指定索引的歌曲
     */
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
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
    }

    fun seekTo(pos: Long) = exoPlayer.seekTo(pos)

    fun removeSong(index: Int) {
        if (index in playbackQueue.value.indices) {
            queueManager.removeSongAt(index)
            exoPlayer.removeMediaItem(index)
        }
    }

    private fun restoreSession() {
        scope.launch {
            playerRepository.getLastPlayedSong()?.let { song ->
                queueManager.setQueue(listOf(song), 0)
                _currentPlayingSong.value = song // [必须补上] 更新UI状态
                Log.d("Mana111",song.toString())
                loadLyric(song)
                exoPlayer.setMediaItem(song.toMediaItem())
                exoPlayer.prepare()
            }
        }
    }
    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                if (exoPlayer.isPlaying) {
                    val currentPos = exoPlayer.currentPosition
                    val lines = currentPlayingSong.value?.lyricList ?: emptyList()
                    _playerState.update {
                        it.copy(
                            isPlaying = true,
                            currentPosition = currentPos,
                            duration = exoPlayer.duration,
                            bufferedPosition = exoPlayer.bufferedPosition,
                            currentLyricLineIndex = lines.indexOfLast { line -> line.time <= currentPos }
                        )
                    }
                }
                delay(200)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        _playerState.update { it.copy(isPlaying = false) }
    }
    /**
     * 添加歌曲到队尾
     */
    fun addSongToQueue(song: Song) {
        queueManager.addSong(song)
        exoPlayer.addMediaItem(song.toMediaItem())
    }
    private fun setupPlayerListener() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) startProgressUpdate() else stopProgressUpdate()
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) skipToNext() // 自动下一首
            }
        })
    }

    // 新增：异步加载歌词方法
    private fun loadLyric(song: Song) {
        song.lyric?.let { Log.d("Mana111",it) }
        if (song.lyric.isNullOrEmpty()) return
        scope.launch(Dispatchers.IO) {
            try {
                val text = java.net.URL(ResourceUtil.r2(song.lyric)).readText()
                Log.d("Mana111",text)
                val list = LrcHelper.parse(text)
                // 校验ID防止切歌后覆盖错误
                _currentPlayingSong.update { if (it?.id == song.id) it.copy(lyricList = list) else it }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun release() {
        exoPlayer.release()
        progressJob?.cancel()
    }
}