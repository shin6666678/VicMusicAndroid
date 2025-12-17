package com.shin.vicmusic.core.domain.usecase

import com.shin.vicmusic.core.domain.PayType
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.manager.AuthManager
import javax.inject.Inject

class CheckVipPermissionUseCase @Inject constructor(
    private val authManager: AuthManager
) {
    /**
     * 检查是否有播放权限
     * @return true 表示允许播放，false 表示需要 VIP
     */
    operator fun invoke(song: Song): Boolean {
        if (song.payType == PayType.VIP) {
            val user = authManager.currentUser.value
            // 假设 User.kt 中已添加 isVip() 扩展方法
            return user != null && user.isVip()
        }
        return true
    }
}