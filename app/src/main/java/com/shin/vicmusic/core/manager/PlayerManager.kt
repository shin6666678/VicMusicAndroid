package com.shin.vicmusic.core.manager

import android.content.Context
import android.util.Log
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
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
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

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
    private val checkVipPermission: CheckVipPermissionUseCase, // [注入] UseCase
    private val songRepository: SongRepository,
    private val userRepository: UserRepository
) {
    private val TAG = "PlayerManager"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    private val _currentPlayingSong = MutableStateFlow<Song?>(null)
    val currentPlayingSong: StateFlow<Song?> = _currentPlayingSong.asStateFlow()

    val currentQueueIndex: StateFlow<Int> = queueManager.currentIndex
    val playbackQueue: StateFlow<List<Song>> = queueManager.queue

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()//UI 事件流，用于通知 UI 层显示 Toast 或 Dialog
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    // Jobs
    private var progressJob: Job? = null
    private var reportPlayJob: Job? = null // 有效播放计数
    private var reportTimeJob: Job? = null // 听歌时长上报

    private val REPORT_INTERVAL = 60000L // 60秒上报一次
    private val REPORT_SECONDS = 60      // 实际上报的秒数

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
        loadLyric(song)
        performPlay()
        playerRepository.saveLastPlayedSong(song)

        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        _playerState.update { it.copy(isPlaying = true) }
        startProgressUpdate()
        // startProgressUpdate 和 startReportTimeJob 由监听器 onIsPlayingChanged 触发,这里只启动“有效播放计数”任务
        startReportPlayJob(song=song)
    }


    /**
     * 播放指定的歌曲（通常用于点击列表中的某首歌开始播放新列表）
     */
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

    fun addSongToQueue(song: Song) {
        queueManager.addSong(song)
        exoPlayer.addMediaItem(song.toMediaItem())
    }

    // --- 监听与任务调度 ---
    private fun setupPlayerListener() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // 统一管理：播放时开启任务，暂停/停止时关闭任务
                if (isPlaying) {
                    startProgressUpdate()
                    startReportTimeJob() // 只有正在播放时才计时
                } else {
                    stopProgressUpdate()
                    stopReportingDuration()  // 暂停/停止 -> 停止时长上报
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) skipToNext()
            }
        })
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


    /*
    业务上报
     */
    /**
     * 记录有效播放 (Play Count)
     * 规则：切歌重置，当前歌曲播放超过10秒后上报一次
     */
    private fun startReportPlayJob(song: Song){
        reportPlayJob?.cancel()
        reportPlayJob = scope.launch(Dispatchers.IO) {
            try {
                // 设置有效播放阈值，10秒 只有听完10秒，才会往下执行。如果中途切歌，这个协程会被 cancel() 杀掉
                delay(10000)

                // 时间到了，确认为有效播放，触发接口
                songRepository.playSong(song.id)
                Log.d("PlayerManager", "Valid play recorded: ${song.title}")
            } catch (e: Exception) {
                // 如果是 CancellationException (被切歌取消)，属于预期行为，无需处理
                if (e !is CancellationException) {
                    e.printStackTrace()
                }
            }
        }
    }
    /**
     * 听歌时长上报 (Listening Duration)
     * 规则：每播放60秒上报一次
     */
    private fun startReportTimeJob() {
        stopReportingDuration()
        reportTimeJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(REPORT_INTERVAL)
                try {
                    // 调用 Repository 上报
                    userRepository.reportDuration(REPORT_SECONDS)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // 当播放器暂停或停止时调用 (onPause, onStop)
    private fun stopReportingDuration() {
        reportTimeJob?.cancel()
        reportTimeJob = null
    }


    /*
    辅助方法
     */
    private fun restoreSession() {
        scope.launch {
            playerRepository.getLastPlayedSong()?.let { song ->
                queueManager.setQueue(listOf(song), 0)
                _currentPlayingSong.value = song // [必须补上] 更新UI状态
                Log.d("Mana111", song.toString())
                loadLyric(song)
                exoPlayer.setMediaItem(song.toMediaItem())
                exoPlayer.prepare()
            }
        }
    }
    private fun loadLyric(song: Song) {
        if (song.lyric.isEmpty()|| song.lyric == "") return
        scope.launch(Dispatchers.IO) {
            try {
                val text = java.net.URL(ResourceUtil.r2(song.lyric)).readText()
                val list = LrcHelper.parse(text)
                // 校验ID防止切歌后覆盖错误
                _currentPlayingSong.update {
                    if (it?.id == song.id) it.copy(lyricList = list) else it
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun release() {
        exoPlayer.release()
        progressJob?.cancel()
        reportPlayJob?.cancel()
        reportTimeJob?.cancel()
    }
}