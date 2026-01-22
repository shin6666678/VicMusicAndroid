package com.shin.vicmusic.feature.myInfo.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.CommonRepository
import com.shin.vicmusic.core.data.repository.UserRepository
import com.shin.vicmusic.core.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// 用于表示编辑页面的UI状态
data class MyInfoEditUiState(
    val name: String = "",
    val headImg: String = "",
    val bgImg: String = "",
    val slogan: String = "",
    val sex: Int = 0,
    val mail: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class MyInfoEditViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val commonRepository: CommonRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyInfoEditUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // 使用当前登录用户的信息来预填充表单
        authManager.currentUser.value?.let { user ->
            _uiState.update {
                it.copy(
                    name = user.name,
                    headImg = user.headImg,
                    mail = user.mail,
                    slogan = user.slogan
                )
            }
        }
    }

    // 当输入框内容变化时，更新状态
    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onSloganChange(newSlogan: String) {
        _uiState.update { it.copy(slogan = newSlogan) }
    }

    fun onSexChange(newSex: Int) {
        _uiState.update { it.copy(sex = newSex) }
    }

    // 当用户选择了新的头像或背景图时（此处仅为示例，后续需集成图片选择器）
    fun onNewAvatarSelected(localUri: String) {
        _uiState.update { it.copy(headImg = localUri) }
    }

    fun onNewBackgroundSelected(localUri: String) {
        _uiState.update { it.copy(bgImg = localUri) }
    }

    fun saveChanges() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // --- 核心业务逻辑 (将在实现Repository后完成) ---
            // 1. 检查头像/背景图是否有变化（URI不是http链接），如果有，则调用`userRepository.uploadFile()`
            // 2. 获取上传后的文件名
            // 3. 构建 UserUpdateReq 对象
            // 4. 调用 `userRepository.updateUser()`
            // 5. 根据结果更新UI状态 (成功或失败)

            // 模拟网络请求
            kotlinx.coroutines.delay(1500)

            // 模拟成功后，更新 AuthManager 中的用户信息，并设置成功状态以便页面可以返回
             authManager.fetchUserInfo()
            _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
        }
    }
}