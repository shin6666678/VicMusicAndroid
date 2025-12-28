package com.shin.vicmusic.feature.checkIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.UserRepository
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckInUiState>(CheckInUiState.Idle)
    val uiState = _uiState.asStateFlow()

    val currentUser = authManager.currentUser

    // 进入页面时刷新一次用户信息，确保状态最新
    init {
        authManager.fetchUserInfo()
    }

    fun checkIn() {
        viewModelScope.launch {
            _uiState.value = CheckInUiState.Loading
            when (val result = userRepository.checkIn()) {
                is Result.Success -> {
                    _uiState.value = CheckInUiState.Success(result.data)
                    authManager.fetchUserInfo() // 签到成功刷新全局用户状态
                }
                is Result.Error -> {
                    _uiState.value = CheckInUiState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    fun resetState() {
        _uiState.value = CheckInUiState.Idle
    }
}

sealed class CheckInUiState {
    object Idle : CheckInUiState()
    object Loading : CheckInUiState()
    data class Success(val message: String) : CheckInUiState()
    data class Error(val message: String) : CheckInUiState()
}