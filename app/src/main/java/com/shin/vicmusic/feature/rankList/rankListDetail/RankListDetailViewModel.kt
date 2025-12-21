package com.shin.vicmusic.feature.rankList.rankListDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.RankListRepository
import com.shin.vicmusic.core.domain.RankListDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankListDetailViewModel @Inject constructor(
    private  val rankListRepository: RankListRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){
    private val rankListId: String = checkNotNull(savedStateHandle["rankListId"])
    private val _rankListDetail = MutableStateFlow<RankListDetail?>(null)
    val rankListDetail = _rankListDetail.asStateFlow()

    init {
        fetchRankListDetail()
    }

    private fun fetchRankListDetail() {
        viewModelScope.launch {
            val rankListResponse = rankListRepository.getRankListById(rankListId)
            if (rankListResponse.status == 0) {
                _rankListDetail.value = rankListResponse.data
            }
        }
    }
}





