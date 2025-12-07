package com.shin.vicmusic.util

import android.icu.util.Calendar

object SuperDateUtil {
    /**
     * 当前年
     */
    fun currentYear(): Int{
        return Calendar.getInstance().get(Calendar.YEAR)
    }
    fun currentDayOfMonth():Int{
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }
}