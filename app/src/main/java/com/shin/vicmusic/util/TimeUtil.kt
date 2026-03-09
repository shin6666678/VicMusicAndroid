package com.shin.vicmusic.util

import android.text.format.DateUtils

object TimeUtil {
    fun getFriendlyTimeSpan(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val aMomentAgo = DateUtils.getRelativeTimeSpanString(
            timestamp, now, DateUtils.MINUTE_IN_MILLIS
        )
        return aMomentAgo.toString()
    }

    fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val hours = totalSeconds / 3600

        return if (hours > 0) {
            // 如果超过一小时，显示 H:MM:SS
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            // 正常显示 M:SS
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}
