package com.shin.vicmusic.feature.myInfo.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.CommonRepository
import com.shin.vicmusic.core.data.repository.UserRepository
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.core.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

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

    private var originalUser: UserInfo? = null

    init {
        authManager.currentUser.value?.let { user ->
            originalUser = user
            _uiState.update {
                it.copy(
                    name = user.name,
                    headImg = user.headImg,
                    bgImg = user.bgImg,
                    slogan = user.slogan,
                    sex = user.sex
                )
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onSloganChange(newSlogan: String) {
        _uiState.update { it.copy(slogan = newSlogan) }
    }

    fun onSexChange(newSex: Int) {
        _uiState.update { it.copy(sex = newSex) }
    }

    fun onNewAvatarSelected(localPath: String) {
        _uiState.update { it.copy(headImg = localPath) }
    }

    // 保存常规用户信息（不包含背景图）
    fun saveChanges() {
        if (uiState.value.isLoading) return
        val currentState = uiState.value
        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(error = "昵称不能为空") }
            return
        }
        if (currentState.name.length > 10) {
            _uiState.update { it.copy(error = "昵称长度不能超过10个字符") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val currentState = uiState.value
            val original = originalUser ?: return@launch

            try {
                var finalHeadImgUrl: String? = null
                if (currentState.headImg != original.headImg) {
                    val file = File(currentState.headImg)
                    if (file.exists()) {
                        when (val uploadResult = commonRepository.uploadImage(file, "user")) {
                            is MyNetWorkResult.Success -> {
                                finalHeadImgUrl = uploadResult.data // 拿到服务器生成的 URL
                            }
                            is MyNetWorkResult.Error -> {
                                throw Exception("图片上传失败: ${uploadResult.message}")
                            }
                        }

                    }
                }

                val nameToUpdate =
                    if (currentState.name != original.name) currentState.name else null
                val sloganToUpdate =
                    if (currentState.slogan != original.slogan) currentState.slogan else null
                val sexToUpdate = if (currentState.sex != original.sex) currentState.sex else null
                if (nameToUpdate == null && sloganToUpdate == null && sexToUpdate == null && finalHeadImgUrl == null) {
                    _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
                    return@launch
                }
                userRepository.updateUserInfo(
                    name = nameToUpdate,
                    slogan = sloganToUpdate,
                    sex = sexToUpdate,
                    headImg = finalHeadImgUrl
                )
                authManager.fetchUserInfo()
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "保存失败，请稍后重试")
                }
            }
        }
    }
}