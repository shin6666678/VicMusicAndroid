package com.shin.vicmusic.feature.rankList.rankList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.RankListRepository
import com.shin.vicmusic.core.domain.RankListPeak
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
            val response = repository.getRankListPeeks()
            if (response.status == 0) {
                _rankListPeaks.value = response.data
            }
        }
    }
}