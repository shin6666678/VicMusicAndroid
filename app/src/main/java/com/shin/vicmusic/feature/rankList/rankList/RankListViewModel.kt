package com.shin.vicmusic.feature.rankList.rankList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.SongRepository
import com.shin.vicmusic.core.network.datasource.RankPageData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankListViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {

    private val _rankData = MutableStateFlow<RankPageData?>(null)
    val rankData = _rankData.asStateFlow()

    init {
        fetchRankData()
    }

    private fun fetchRankData() {
        viewModelScope.launch {
            val response = repository.getRankData()
            if (response.status == 0) {
                _rankData.value = response.data
            }
        }
    }
}