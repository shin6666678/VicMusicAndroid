package com.shin.vicmusic.core.model


data class UiState<T>(
    val data: T,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

fun <T> UiState<T>.loading() = copy(isLoading = true, errorMessage = null)
fun <T> UiState<T>.success(newData: T) = copy(data = newData, isLoading = false, errorMessage = null)
fun <T> UiState<T>.error(msg: String?) = copy(isLoading = false, errorMessage = msg)