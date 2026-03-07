package com.shin.vicmusic.feature.me.purchased

data class PurchasedState(
    val isLoading: Boolean = false,
    val items: List<String> = emptyList()
)

sealed interface PurchasedIntent {
    data object LoadData : PurchasedIntent
}

sealed interface PurchasedEffect {
    data class ShowToast(val message: String) : PurchasedEffect
}
