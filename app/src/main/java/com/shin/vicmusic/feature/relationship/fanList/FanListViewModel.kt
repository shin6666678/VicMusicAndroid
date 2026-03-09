package com.shin.vicmusic.feature.relationship.fanList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.RelationshipRepository
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.PageResult
import com.shin.vicmusic.core.domain.UserInfo
import com.shin.vicmusic.core.model.UiState
import com.shin.vicmusic.core.model.error
import com.shin.vicmusic.core.model.loading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FanListUiState(
    val pageResult: PageResult<UserInfo> = PageResult(
        items = emptyList(),
        total = 0,
        page = 0,
        hasMore = true
    ),
)

@HiltViewModel
class FanListViewModel @Inject constructor(
    private val relationshipRepository: RelationshipRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState(data = FanListUiState()))
    val uiState = _uiState.asStateFlow()

    private var isFetching = false

    fun loadData() {
        if (isFetching) return

        val currentData = _uiState.value.data.pageResult
        if (!currentData.hasMore) return

        val pageToLoad = currentData.page + 1

        viewModelScope.launch {
            isFetching = true
            _uiState.update { it.loading() }

            when (val res = relationshipRepository.getFans(pageToLoad, 10)) {
                is MyNetWorkResult.Success -> {
                    _uiState.update { baseState ->
                        baseState.copy(
                            isLoading = false,
                            errorMessage = null,
                            data = FanListUiState(
                                pageResult = PageResult(
                                    items = currentData.items + res.data.items,
                                    total = res.data.total,
                                    page = res.data.page,
                                    hasMore = res.data.hasMore
                                )
                            )
                        )
                    }
                }
                is MyNetWorkResult.Error -> {
                    _uiState.update { it.error(res.message) }
                }
            }
            isFetching = false
        }
    }
}