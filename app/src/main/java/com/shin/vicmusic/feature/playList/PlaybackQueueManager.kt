package com.shin.vicmusic.feature.playList


import javax.inject.Inject
import javax.inject.Singleton
import com.shin.vicmusic.core.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton // 确保它是单例，全局共享同一个播放队列
class PlaybackQueueManager @Inject constructor() {

    // 队列状态 (不再在 ViewModel 中维护)
    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    // ⭐ 负责计算下一首索引的纯逻辑
    fun getNextIndex(): Int {
        val current = _currentIndex.value
        val listSize = _queue.value.size
        return if (listSize > 0) (current + 1) % listSize else -1
    }

    // ⭐ 负责计算上一首索引的纯逻辑
    fun getPreviousIndex(): Int {
        val current = _currentIndex.value
        val listSize = _queue.value.size
        return if (listSize > 0) (current - 1 + listSize) % listSize else -1
    }

    // ⭐ 负责设置新队列和索引
    fun setQueue(newQueue: List<Song>, startIndex: Int) {
        _queue.value = newQueue
        _currentIndex.value = startIndex
        // 注意：不在这里操作 ExoPlayer
    }

    // ⭐ 负责添加歌曲到队列
    fun addSong(song: Song) {
        _queue.update { it + song }
        // 注意：不在这里操作 ExoPlayer
    }

    // ⭐ 负责更新当前索引
    fun updateIndex(newIndex: Int) {
        _currentIndex.value = newIndex
    }

    // 获取当前播放歌曲
    fun getCurrentSong(): Song? {
        val index = _currentIndex.value
        val list = _queue.value
        return if (index in list.indices) list[index] else null
    }
}