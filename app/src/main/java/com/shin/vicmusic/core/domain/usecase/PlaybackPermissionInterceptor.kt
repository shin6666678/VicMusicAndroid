package com.shin.vicmusic.core.domain.usecase

import com.shin.vicmusic.core.domain.Song
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackPermissionInterceptor @Inject constructor(
    private val checkVipPermission: CheckVipPermissionUseCase
) {
    sealed class Result {
        object Allowed : Result()
        object ShowCopyrightDialog : Result()
        object ShowVipDialog : Result()
        object ForceSkip : Result()
    }

    fun check(
        song: Song?,
        currentPosition: Long,
        isPlaying: Boolean,
        isSongDetailVisible: Boolean
    ): Result {
        if (song == null) return Result.Allowed

        // 1. 版权检查
        if (!song.isCopyright) {
            return Result.ShowCopyrightDialog
        }

        // 2. VIP 试听限制 (10秒)
        if (!checkVipPermission(song) && currentPosition >= 10000) {
            return if (isSongDetailVisible) Result.ShowVipDialog else Result.ForceSkip
        }

        return Result.Allowed
    }
}