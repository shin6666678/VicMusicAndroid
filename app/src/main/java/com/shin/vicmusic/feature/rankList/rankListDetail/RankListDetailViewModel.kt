package com.shin.vicmusic.feature.rankList.rankListDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.RankListRepository
import com.shin.vicmusic.core.domain.RankListDetail
import com.shin.vicmusic.core.domain.Result
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
            // [修改] 处理 Result<RankListDetail>
            when (val result = rankListRepository.getRankListById(rankListId)) {
                is Result.Success -> {
                    _rankListDetail.value = result.data
                }
                is Result.Error -> {
                    // 这里可以处理错误，比如设置一个 error 状态 string
                    // _error.value = result.message
                }
            }
        }
    }
}





