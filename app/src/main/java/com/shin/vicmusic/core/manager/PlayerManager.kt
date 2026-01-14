package com.shin.vicmusic.core.manager

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.shin.vicmusic.core.data.repository.PlayerRepository
import com.shin.vicmusic.core.data.repository.SongRepository
import com.shin.vicmusic.core.data.repository.UserRepository
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.domain.usecase.CheckVipPermissionUseCase
import com.shin.vicmusic.core.service.PlaybackService
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

data class PlayerState(
    val isPlaying: Boolean = false,
    val duration: Long = 0,
    val currentPosition: Long = 0,
    val bufferedPosition: Long = 0,
    val currentLyricLineIndex: Int = -1,
)

@UnstableApi
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

    private var mediaController: MediaController? = null

    var isSongDetailVisible: Boolean = false

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
        initializeMediaController()
        scope.launch {
            currentPlayingSong.collect { song ->
                playerRepository.saveLastPlayedSong(song)
            }
        }
        restoreLastSession()
    }

    private fun initializeMediaController() {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener(
            {
                mediaController = controllerFuture.get()
                mediaController?.addListener(playerListener)
                syncStateWithController()
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    private fun syncStateWithController() {
        val controller = mediaController ?: return
        val currentMediaItem = controller.currentMediaItem
        if (currentMediaItem != null) {
            val song = playbackQueue.value.find { it.id == currentMediaItem.mediaId }
            _currentPlayingSong.value = song
            if (song != null) loadLyric(song)
        } else {
            // B. 【新增】如果 Service 是空的（冷啟動），檢查我們是否從本地恢復了數據
            val restoredSong = _currentPlayingSong.value
            if (restoredSong != null && controller.mediaItemCount == 0) {
                // 將恢復的歌曲設置給 Player，並準備好，但不自動播放
                val queue = playbackQueue.value
                if (queue.isNotEmpty()) {
                    controller.setMediaItems(queue.map { it.toMediaItem() })
                    val index = queueManager.currentIndex.value.coerceAtLeast(0)
                    controller.seekTo(index, 0)
                    controller.prepare()
                    controller.pause() // 關鍵：恢復後暫停，等待用戶點擊
                }
            }
        }

        updatePlayerState()
        toggleProgressUpdate()
    }
    private fun restoreLastSession() {
        scope.launch {
            try {
                val lastSong = playerRepository.getLastPlayedSong()

                if (lastSong != null) {
                    _currentPlayingSong.value = lastSong
                    if (playbackQueue.value.isEmpty()) {
                        queueManager.setQueue(listOf(lastSong), 0)
                    }
                    loadLyric(lastSong)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playSong(song: Song, queue: List<Song>? = null) {
        val controller = mediaController ?: return
        scope.launch {
            val newQueue = queue ?: listOf(song)
            val index = newQueue.indexOfFirst { it.id == song.id }
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
            queueManager.updateIndex(index)
            controller.seekToDefaultPosition(index)
            controller.play()
        }
    }

    fun skipToNext() = mediaController?.seekToNextMediaItem()
    fun skipToPrevious() = mediaController?.seekToPreviousMediaItem()

    fun togglePlayPause() {
        val controller = mediaController ?: return
        if (controller.playbackState == Player.STATE_ENDED) {
            controller.seekTo(0)
            controller.play()
        } else {
            if (controller.playWhenReady) controller.pause() else controller.play()
        }
    }

    fun seekTo(pos: Long) {
        isSeeking = true
        mediaController?.seekTo(pos)
        _playerState.update { it.copy(currentPosition = pos) }
    }

    fun removeSong(index: Int) {
        if (index in playbackQueue.value.indices) {
            queueManager.removeSongAt(index)
            mediaController?.removeMediaItem(index)
        }
    }

    fun addSongToQueue(song: Song) {
        queueManager.addSong(song)
        mediaController?.addMediaItem(song.toMediaItem())
    }

    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            updatePlayerState()
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            toggleProgressUpdate()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            toggleProgressUpdate()
            if (isPlaying) startReportTimeJob() else stopReportingDuration()
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
                val index = playbackQueue.value.indexOf(song)
                if (index != -1) {
                    queueManager.updateIndex(index)
                    _currentPlayingSong.value = song
                    if (song != null) {
                        loadLyric(song)
                        startReportPlayJob(song)
                    }
                }
            }
        }

        override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
            if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                isSeeking = false
                updatePlayerState()
            }
        }
    }

    private fun toggleProgressUpdate() {
        val controller = mediaController ?: return
        val shouldUpdate = controller.isPlaying || controller.isLoading
        if (shouldUpdate) startProgressUpdate() else stopProgressUpdate()
    }

    private fun updatePlayerState() {
        val controller = mediaController ?: return
        val currentPos = controller.currentPosition

        val currentSong = _currentPlayingSong.value
        if (currentSong != null && !checkVipPermission(currentSong) && currentPos >= 10000) {
            if (controller.isPlaying) {
                controller.pause()
                if (isSongDetailVisible) scope.launch { _uiEvent.emit("SHOW_VIP_DIALOG") } else skipToNext()
            }
        }

        val isUiPlaying = controller.isPlaying ||
                (controller.playbackState == Player.STATE_BUFFERING && controller.playWhenReady)

        _playerState.update {
            it.copy(
                isPlaying = isUiPlaying,
                duration = controller.duration.coerceAtLeast(0),
                bufferedPosition = controller.bufferedPosition,
                currentPosition = if (isSeeking) it.currentPosition else currentPos,
                currentLyricLineIndex = currentPlayingSong.value?.lyricList?.indexOfLast { line -> line.time <= currentPos } ?: -1
            )
        }
    }

    private fun startProgressUpdate() {
        if (progressJob?.isActive == true) return
        progressJob = scope.launch {
            while (isActive) {
                if (!isSeeking) {
                    updatePlayerState()
                }
                delay(200)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        updatePlayerState()
    }

    private fun startReportPlayJob(song: Song) {
        reportPlayJob?.cancel()
        reportPlayJob = scope.launch(Dispatchers.IO) {
            try {
                delay(10000)
                songRepository.playSong(song.id)
                Log.d(TAG, "Valid play recorded: ${song.title}")
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

    private fun loadLyric(song: Song) {
        if (song.lyric.isEmpty()) return
        scope.launch(Dispatchers.IO) {
            try {
                val text = java.net.URL(ResourceUtil.r2(song.lyric)).readText()
                val list = LrcHelper.parse(text)
                _currentPlayingSong.update { if (it?.id == song.id) it.copy(lyricList = list) else it }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun release() {
        mediaController?.release()
        progressJob?.cancel()
        reportPlayJob?.cancel()
        reportTimeJob?.cancel()
    }
}