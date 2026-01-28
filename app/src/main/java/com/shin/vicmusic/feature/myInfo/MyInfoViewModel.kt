package com.shin.vicmusic.feature.myInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.CommonRepository
import com.shin.vicmusic.core.data.repository.UserRepository
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class MyInfoUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class MyInfoViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val commonRepository: CommonRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val currentUser = authManager.currentUser
    val isLoggedIn = authManager.isLoggedIn

    private val _uiState = MutableStateFlow(MyInfoUiState())
    val uiState = _uiState.asStateFlow()

    fun refresh() {
        authManager.fetchUserInfo()
    }

    fun clearMessage() {
        _uiState.update { it.copy(error = null, message = null) }
    }

    // 独立修改背景图逻辑
    fun updateUserBg(localPath: String) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, message = null) }

            try {
                val file = File(localPath)
                if (!file.exists()) {
                    throw Exception("文件不存在")
                }

                // 1. 上传图片
                val uploadResult = commonRepository.uploadImage(file, "user")
                val finalBgImgUrl = when (uploadResult) {
                    is Result.Success -> uploadResult.data
                    is Result.Error -> throw Exception(uploadResult.message ?: "图片上传失败")
                }

                // 2. 更新用户背景图 (单独接口)
                val updateResult = userRepository.updateUserBgImg(finalBgImgUrl)
                if (updateResult is Result.Error) {
                    throw Exception(updateResult.message ?: "更新背景失败")
                }

                // 3. 刷新用户信息
                authManager.fetchUserInfo()

                _uiState.update { it.copy(isLoading = false, message = "背景图更换成功") }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "操作失败") }
            }
        }
    }
}