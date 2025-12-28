package com.shin.vicmusic.feature.vip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.AuthRepository
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.VipProduct
import com.shin.vicmusic.core.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VipViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authManager: AuthManager
): ViewModel() {


    private val _message = MutableSharedFlow<String>()
    val message: SharedFlow<String> = _message.asSharedFlow()


    fun purchaseVip() {

        val targetLevel = 1
        viewModelScope.launch {
            when(val result = authRepository.changeVipLevel(targetLevel)) {
                is Result.Success -> {
                    _message.emit("购买成功，您已成为VIP用户！")
                    authManager.fetchUserInfo()
                }
                is Result.Error -> {
                    _message.emit(result.message)
                }
            }
        }
    }
}