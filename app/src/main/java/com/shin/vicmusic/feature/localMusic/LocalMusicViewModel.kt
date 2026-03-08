package com.shin.vicmusic.feature.localMusic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.LocalMusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalMusicViewModel @Inject constructor(
    private val localMusicRepository: LocalMusicRepository
) : ViewModel() {

    // 对接 Repository 暴露的 Flow，转换成StateFlow
    val localSongs = localMusicRepository.observeLocalMusic()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun scanLocalMusic() {
        viewModelScope.launch {
            localMusicRepository.scanAndSaveLocalMusic()
        }
    }
}