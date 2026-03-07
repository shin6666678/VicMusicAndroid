package com.shin.vicmusic.feature.me.audio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(AudioState())
    val state: StateFlow<AudioState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AudioEffect>()
    val effect: SharedFlow<AudioEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: AudioIntent) {
        when (intent) {
            is AudioIntent.LoadData -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // 模拟加载数据
            val mockData = listOf("有声书 1", "有声书 2", "播客 1")
            _state.update { it.copy(isLoading = false, items = mockData) }
        }
    }
}
