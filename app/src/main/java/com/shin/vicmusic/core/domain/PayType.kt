package com.shin.vicmusic.core.domain

enum class PayType(val value: Int, val description: String) {
    FREE(0, "免费(FREE)"),
    VIP(1, "VIP专享(VIP)"),
    PAY(2, "付费(PAY)");

    companion object {
        fun fromValue(value: Int): PayType = entries.firstOrNull { it.value == value } ?: FREE
    }
}