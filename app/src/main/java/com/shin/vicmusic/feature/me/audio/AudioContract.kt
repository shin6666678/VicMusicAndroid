package com.shin.vicmusic.feature.me.audio

data class AudioState(
    val isLoading: Boolean = false,
    val items: List<String> = emptyList() // 临时使用 String 代替具体的数据模型
)

sealed interface AudioIntent {
    data object LoadData : AudioIntent
}

sealed interface AudioEffect {
    data class ShowToast(val message: String) : AudioEffect
}
