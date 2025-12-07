package com.shin.vicmusic.util

fun formatTime(millis: Long): String {
    val minutes = millis / 1000 / 60
    val seconds = millis / 1000 % 60
    return String.format("%02d:%02d", minutes, seconds)
}