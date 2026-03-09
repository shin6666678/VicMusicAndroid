package com.shin.vicmusic.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.manager.SongActionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 自动同步全局歌曲状态到当前 ViewModel 的列表状态中
 * @param source 数据源 Flow (例如 _datum)
 * @param songActionManager 全局管理类
 */
/**
 * 将其定义在单独的工具类文件中
 */
fun ViewModel.syncSongStatus(
    source: MutableStateFlow<List<Song>>,
    songActionManager: SongActionManager
) {
    // 这里直接使用 ViewModel 自带的 viewModelScope
    viewModelScope.launch {
        songActionManager.songUpdateEvent.collect { updatedSong ->
            source.update { list ->
                // 仅当列表中存在该歌曲时才更新，优化性能
                if (list.any { it.id == updatedSong.id }) {
                    list.map { if (it.id == updatedSong.id) updatedSong else it }
                } else {
                    list
                }
            }
        }
    }
}
/**
 * 通用的状态同步工具
 * @param source 需要更新的 StateFlow
 * @param songActionManager 全局管理类
 * @param updateLogic 定义如何将更新后的 Song 塞回你的数据结构中
 */
fun <T> ViewModel.syncCustomStatus(
    source: MutableStateFlow<T>,
    songActionManager: SongActionManager,
    updateLogic: (T, Song) -> T
) {
    viewModelScope.launch {
        songActionManager.songUpdateEvent.collect { updatedSong ->
            source.update { currentData ->
                updateLogic(currentData, updatedSong)
            }
        }
    }
}