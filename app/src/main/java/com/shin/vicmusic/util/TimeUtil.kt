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
}
