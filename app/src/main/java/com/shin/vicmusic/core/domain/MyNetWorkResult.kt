package com.shin.vicmusic.core.domain

sealed class MyNetWorkResult<out T> {
    data class Success<out T>(val data: T) : MyNetWorkResult<T>()
    data class Error(val message: String) : MyNetWorkResult<Nothing>()
}