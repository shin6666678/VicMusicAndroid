package com.shin.vicmusic.core.manager

import com.shin.vicmusic.core.data.repository.LikeRepository
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongActionManager @Inject constructor(
    private val likeRepository: LikeRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // 用于通知 UI 某个歌曲状态发生了变化（简单的事件总线）
    private val _songUpdateEvent = MutableSharedFlow<Song>()
    val songUpdateEvent: SharedFlow<Song> = _songUpdateEvent.asSharedFlow()

    fun toggleLike(song: Song) {
        scope.launch {
            // 1. 乐观更新：先通知 UI 改变状态（假设成功）
            val newSong = song.copy(isLiked = !song.isLiked)
            _songUpdateEvent.emit(newSong)

            // 2. 网络请求
            val result = likeRepository.likeSong(song.id)

            // 3. 如果失败，回滚状态
            if (result is Result.Error) {
                _songUpdateEvent.emit(song) // 发回原始状态
            }
        }
    }
}