package com.shin.vicmusic.feature.rankList.rankList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.RankListRepository
import com.shin.vicmusic.core.domain.RankListPeak
import com.shin.vicmusic.core.domain.MyNetWorkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankListViewModel @Inject constructor(
    private val repository: RankListRepository
) : ViewModel() {

    private val _rankListPeaks = MutableStateFlow<List<RankListPeak>?>(null)
    val rankListPeaks = _rankListPeaks.asStateFlow()

    init {
        fetchRankData()
    }

    private fun fetchRankData() {
        viewModelScope.launch {
            // [修改] 处理 Result<List<RankListPeak>>
            when (val result = repository.getRankListPeeks()) {
                is MyNetWorkResult.Success -> {
                    _rankListPeaks.value = result.data
                }
                is MyNetWorkResult.Error -> {
                    // 这里可以处理错误，例如显示 Toast 或 Error State
                    // Log.e("RankListViewModel", result.message)
                }
            }
        }
    }
}