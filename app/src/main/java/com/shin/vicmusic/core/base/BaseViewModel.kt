package com.shin.vicmusic.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * 所有 ViewModel 的基类，提供统一的错误事件处理。
 */
abstract class BaseViewModel : ViewModel() {

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent: SharedFlow<String> = _errorEvent

    /**
     * 发送一个错误事件给 UI 层。
     * UI 层可以监听 [errorEvent] 来显示 Toast 或 SnackBar。
     */
    protected fun sendErrorEvent(message: String) {
        viewModelScope.launch {
            _errorEvent.emit(message)
        }
    }
}
