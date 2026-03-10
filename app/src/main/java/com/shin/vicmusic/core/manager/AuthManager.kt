package com.shin.vicmusic.core.manager

import com.shin.vicmusic.core.config.AppGlobalData
import com.shin.vicmusic.core.data.repository.AuthRepository
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.core.datastore.UserPrefsProto
import androidx.datastore.core.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager, // 必须注入
    private val userPrefsDataStore: DataStore<UserPrefsProto>
) {
    private val scope = CoroutineScope(SupervisorJob())

    // true 表示已登录，false 表示未登录，null 表示尚未确定
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    // [新增] 当前用户信息状态 (Current User State)
    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: StateFlow<UserInfo?> = _currentUser

    init {
        // [修改2] 初始化时监听 Token，如果有则自动恢复登录
        scope.launch {
            // 从 Proto DataStore 快速读取用户信息缓存
            launch {
                val proto = userPrefsDataStore.data.first()
                if (proto.id.isNotEmpty()) {
                    _currentUser.value = proto.toUserInfo()
                }
            }

            tokenManager.tokenFlow.collect { savedToken ->
                if (!savedToken.isNullOrBlank()) {
                    AppGlobalData.token = savedToken
                    // 仅当状态未设置时才调用，避免循环
                    if (_isLoggedIn.value != true) {
                        setLoginStatus(true)
                    }
                }
            }
        }
    }

    // 供LoginViewModel或其他认证流程调用，以更新全局登录状态
    fun setLoginStatus(loggedIn: Boolean) {
        if (_isLoggedIn.value == loggedIn) return
        _isLoggedIn.value = loggedIn
        if (loggedIn) {
            fetchUserInfo() // [新增] 登录成功自动获取用户信息
        } else {
            _currentUser.value = null // 登出清空信息
            scope.launch { 
                tokenManager.clearToken() 
                userPrefsDataStore.updateData { it.toBuilder().clear().build() }
            }
        }
    }

    // 获取用户信息方法
    fun fetchUserInfo() {
        scope.launch {
            val result =  authRepository.getUserInfo()
            when(result){
                is MyNetWorkResult.Success->{
                    _currentUser.value = result.data
                    // 确保登录状态为 true
                    if (_isLoggedIn.value != true) _isLoggedIn.value = true
                    // 异步保存到 Proto DataStore
                    userPrefsDataStore.updateData { result.data.toProto() }
                }
                is MyNetWorkResult.Error->{
                    android.util.Log.e("AuthManager", "fetchUserInfo Error: ${result.message}")
                    // Optionally set login status to false or clear token if it's an authorization error
                    // But for now just log it so we can trace it.
                }
            }

        }
    }

    // Mapper Helpers to protect domain layer from Protobuf details
    private fun UserInfo.toProto(): UserPrefsProto {
        return UserPrefsProto.newBuilder()
            .setId(this.id)
            .setName(this.name)
            .setHeadImg(this.headImg)
            .setSlogan(this.slogan)
            .setSex(this.sex)
            .setBgImg(this.bgImg)
            .setPoints(this.points)
            .setMail(this.mail)
            .setFollowCount(this.followCount)
            .setFollowerCount(this.followerCount)
            .setLevel(this.level)
            .setVipLevel(this.vipLevel)
            .setHeardCount(this.heardCount)
            .setIsFollowing(this.isFollowing)
            .setIsFollowingMe(this.isFollowingMe)
            .setExperience(this.experience)
            .setNextLevelExp(this.nextLevelExp)
            .setTotalListenTime(this.totalListenTime)
            .build()
    }

    private fun UserPrefsProto.toUserInfo(): UserInfo {
        return UserInfo(
            id = this.id,
            name = this.name,
            headImg = this.headImg,
            slogan = this.slogan,
            sex = this.sex,
            bgImg = this.bgImg,
            points = this.points,
            mail = this.mail,
            followCount = this.followCount,
            followerCount = this.followerCount,
            level = this.level,
            vipLevel = this.vipLevel,
            heardCount = this.heardCount,
            isFollowing = this.isFollowing,
            isFollowingMe = this.isFollowingMe,
            experience = this.experience,
            nextLevelExp = this.nextLevelExp,
            totalListenTime = this.totalListenTime
        )
    }
}