package com.shin.vicmusic.feature.vip

import androidx.lifecycle.ViewModel
import com.shin.vicmusic.core.domain.VipProduct
import com.shin.vicmusic.feature.auth.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class VipViewModel @Inject constructor( // 2. 注入 AuthManager
    private val authManager: AuthManager
): ViewModel() {

    private val _vipProducts = MutableStateFlow<List<VipProduct>>(emptyList())
    val vipProducts: StateFlow<List<VipProduct>> = _vipProducts.asStateFlow()

    private val _selectedProductIndex = MutableStateFlow(1) // 默认选中中间的（通常是推荐的）
    val selectedProductIndex: StateFlow<Int> = _selectedProductIndex.asStateFlow()

    init {
        loadMockData()
    }

    fun selectProduct(index: Int) {
        _selectedProductIndex.value = index
    }

    private fun loadMockData() {
        // 模拟数据
        val mockData = listOf(
            VipProduct(
                id = "1",
                name = "连续包月",
                price = "USDT1",
                originalPrice = "USDT2",
                description = "首月特惠 ¥0"
            ),
            VipProduct(
                id = "2",
                name = "12个月",
                price = "USDT3",
                originalPrice = "USDT4",
                discountTag = "超值推荐",
                isRecommended = true,
                description = "折合 USDT0/月"
            ),
            VipProduct(
                id = "3",
                name = "3个月",
                price = "USDT5",
                originalPrice = "USDT6",
                description = "折合 USDT0/月"
            )
        )
        _vipProducts.value = mockData
    }
}