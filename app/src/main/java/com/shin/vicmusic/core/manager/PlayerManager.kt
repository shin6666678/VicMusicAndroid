package com.shin.vicmusic.core.manager

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.shin.vicmusic.core.data.repository.PlayerRepository
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.domain.usecase.GetLyricsUseCase
import com.shin.vicmusic.core.domain.usecase.PlaybackPermissionInterceptor
import com.shin.vicmusic.core.service.PlaybackService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

data class PlayerUiState(
    val song: Song? = null,
    val isPlaying: Boolean = false,
    val duration: Long = 0,
    val currentPosition: Long = 0,
    val bufferedPosition: Long = 0,
    val currentLyricLineIndex: Int = -1,
    val isSongDetailVisible: Boolean = false
)

sealed class PlayerUiEvent {
    object ShowCopyrightDialog : PlayerUiEvent()
    object ShowVipDialog : PlayerUiEvent()
    data class Error(val message: String) : PlayerUiEvent()
}

@UnstableApi
@Singleton
class PlayerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val queueManager: PlaybackQueueManager,
    private val musicAnalyticsManager: MusicAnalyticsManager,
    private val getLyricsUseCase: GetLyricsUseCase,
    private val playerRepository: PlayerRepository,
    private val playbackPermissionInterceptor: PlaybackPermissionInterceptor,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var mediaController: MediaController? = null
    private var progressJob: Job? = null
    private var isSeeking = false

    // --- 单一数据源 ---
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    // --- 强类型事件 ---
    private val _uiEvent = Channel<PlayerUiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    // 公开快捷变量（供其它业务逻辑方便获取）
    val currentQueueIndex: StateFlow<Int> = queueManager.currentIndex
    val playbackQueue: StateFlow<List<Song>> = queueManager.queue

    init {

        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            mediaController = controllerFuture.get()
            mediaController?.addListener(playerListener)
            syncStateWithController()
        }, ContextCompat.getMainExecutor(context))
        // 自动保存最后播放
        scope.launch {
            uiState.collect { state ->
                playerRepository.saveLastPlayedSong(state.song)
            }
        }
        restoreLastSession()
    }

    // --- UI 操作入口 ---

    fun setSongDetailVisible(visible: Boolean) {
        _uiState.update { it.copy(isSongDetailVisible = visible) }
    }

    fun playSong(song: Song, queue: List<Song>? = null) {
        val controller = mediaController ?: return
        scope.launch {
            // 播放前检查拦截器（例如版权判断）
            val intercept = playbackPermissionInterceptor.check(song, 0, false, _uiState.value.isSongDetailVisible)
            if (intercept is PlaybackPermissionInterceptor.Result.ShowCopyrightDialog) {
                _uiState.update { it.copy(song = song) }
                _uiEvent.send(PlayerUiEvent.ShowCopyrightDialog)
                return@launch
            }

            val newQueue = queue ?: listOf(song)
            val index = newQueue.indexOfFirst { it.id == song.id }.coerceAtLeast(0)

            queueManager.setQueue(newQueue, index)
            controller.setMediaItems(newQueue.map { it.toMediaItem() })
            controller.seekTo(index, 0)
            controller.prepare()
            controller.play()
        }
    }

    fun playAtIndex(index: Int) {
        val controller = mediaController ?: return
        val queue = playbackQueue.value
        if (index in queue.indices) {
            val song = queue[index]
            // 检查拦截器
            val intercept = playbackPermissionInterceptor.check(song, 0, false, _uiState.value.isSongDetailVisible)
            if (intercept is PlaybackPermissionInterceptor.Result.ShowCopyrightDialog) {
                _uiState.update { it.copy(song = song) }
                scope.launch { _uiEvent.send(PlayerUiEvent.ShowCopyrightDialog) }
                return
            }
            queueManager.updateIndex(index)
            controller.seekToDefaultPosition(index)
            controller.prepare()
            controller.play()
        }
    }

    fun togglePlayPause() {
        val controller = mediaController ?: return
        val song = _uiState.value.song
        if (controller.playbackState == Player.STATE_ENDED) {
            controller.seekTo(0)
            controller.play()
        } else {
            // 如果没版权且未在播放（即处于被拦截状态），点击播放应重新弹窗
            if (song != null && !song.isCopyright && !controller.playWhenReady) {
                scope.launch { _uiEvent.send(PlayerUiEvent.ShowCopyrightDialog) }
                return
            }
            if (controller.playWhenReady) controller.pause() else controller.play()
        }
    }

    fun seekTo(pos: Long) {
        isSeeking = true
        mediaController?.seekTo(pos)
        _uiState.update { it.copy(currentPosition = pos) }
    }

    fun skipToNext() = mediaController?.seekToNextMediaItem()
    fun skipToPrevious() = mediaController?.seekToPreviousMediaItem()


    private fun syncStateWithController() {
        val controller = mediaController ?: return
        val currentMediaItem = controller.currentMediaItem

        if (currentMediaItem != null) {
            val song = playbackQueue.value.find { it.id == currentMediaItem.mediaId }
            _uiState.update { it.copy(song = song) }
            song?.let { loadLyric(it) }
        } else {
            // 处理冷启动从本地恢复
            val restoredSong = _uiState.value.song
            if (restoredSong != null && controller.mediaItemCount == 0) {
                val queue = playbackQueue.value
                if (queue.isNotEmpty()) {
                    controller.setMediaItems(queue.map { it.toMediaItem() })
                    controller.seekTo(queueManager.currentIndex.value.coerceAtLeast(0), 0)
                    controller.prepare()
                    controller.pause()
                }
            }
        }
        updatePlayerState()
        toggleProgressUpdate()
    }

    private fun restoreLastSession() {
        scope.launch {
            playerRepository.getLastPlayedSong()?.let { lastSong ->
                _uiState.update { it.copy(song = lastSong) }
                if (queueManager.queue.value.isEmpty()) {
                    queueManager.setQueue(listOf(lastSong), 0)
                }
                loadLyric(lastSong)
            }
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) = updatePlayerState()

        override fun onIsLoadingChanged(isLoading: Boolean) = toggleProgressUpdate()

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            toggleProgressUpdate()
            if (isPlaying) musicAnalyticsManager.startDurationReport() else musicAnalyticsManager.stopDurationReport()
        }

        override fun onPlaybackStateChanged(state: Int) {
            if (state == Player.STATE_READY) {
                isSeeking = false
                updatePlayerState()
            }
        }

        override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
            mediaItem?.mediaId?.let { songId ->
                val song = playbackQueue.value.find { it.id == songId }
                _uiState.update { it.copy(song = song) }
                if (song != null) {
                    if (!song.isCopyright) {
                        mediaController?.pause()
                        scope.launch { _uiEvent.send(PlayerUiEvent.ShowCopyrightDialog) }
                    } else {
                        val index = playbackQueue.value.indexOf(song)
                        queueManager.updateIndex(index)
                        loadLyric(song)
                        musicAnalyticsManager.startTrackPlayReport(song.id)
                    }
                }
            }
        }
    }

    private fun updatePlayerState() {
        val controller = mediaController ?: return
        val state = _uiState.value
        val pos = controller.currentPosition
        val song = state.song

        // 1. 业务规则拦截 (VIP/版权)
        val intercept = playbackPermissionInterceptor.check(song, pos, controller.isPlaying, state.isSongDetailVisible)
        handleInterceptResult(intercept, controller)

        // 2. 原子化更新 UI 状态
        _uiState.update {
            it.copy(
                isPlaying = controller.isActuallyPlaying(),
                duration = controller.duration.coerceAtLeast(0),
                bufferedPosition = controller.bufferedPosition,
                currentPosition = if (isSeeking) it.currentPosition else pos,
                currentLyricLineIndex = song?.lyricList?.indexOfLast { line -> line.time <= pos } ?: -1
            )
        }
    }

    private fun handleInterceptResult(result: PlaybackPermissionInterceptor.Result, controller: Player) {
        scope.launch {
            when(result) {
                PlaybackPermissionInterceptor.Result.ShowCopyrightDialog -> {
                    controller.pause()
                    _uiEvent.send(PlayerUiEvent.ShowCopyrightDialog)
                }
                PlaybackPermissionInterceptor.Result.ShowVipDialog -> {
                    controller.pause()
                    _uiEvent.send(PlayerUiEvent.ShowVipDialog)
                }
                PlaybackPermissionInterceptor.Result.ForceSkip -> skipToNext()
                else -> {}
            }
        }
    }

    private fun loadLyric(song: Song) {
        if (song.lyric.isEmpty()) return
        scope.launch {
            val lyrics = getLyricsUseCase(song.lyric)
            if (lyrics.isNotEmpty()) {
                _uiState.update {
                    if (it.song?.id == song.id) it.copy(song = it.song.copy(lyricList = lyrics)) else it
                }
            }
        }
    }

    // --- 播放器工具函数 ---
    private fun Player.isActuallyPlaying() = isPlaying || (playbackState == Player.STATE_BUFFERING && playWhenReady)

    private fun toggleProgressUpdate() {
        val controller = mediaController ?: return
        if (controller.isPlaying || controller.isLoading) startProgressUpdate() else stopProgressUpdate()
    }

    private fun startProgressUpdate() {
        if (progressJob?.isActive == true) return
        progressJob = scope.launch {
            while (isActive) {
                if (!isSeeking) updatePlayerState()
                delay(200)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        updatePlayerState()
    }

    fun release() {
        mediaController?.release()
        progressJob?.cancel()
        musicAnalyticsManager.cancelAll()
    }

    // ... 在 PlayerManager 类中添加以下方法

    fun addSongToQueue(song: Song) {
        // 1. 更新本地逻辑队列
        queueManager.addSong(song)

        // 2. 通知 Media3 播放器添加这一项
        // 这样当播放器顺序播放到最后时，能无缝切换到新加的歌曲
        mediaController?.addMediaItem(song.toMediaItem())
    }

    fun removeSong(index: Int) {
        if (index in queueManager.queue.value.indices) {
            queueManager.removeSongAt(index)
            mediaController?.removeMediaItem(index)
        }
    }
}