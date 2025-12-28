package com.shin.vicmusic.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.SearchRepository
import com.shin.vicmusic.core.domain.*
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.manager.SearchHistoryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val searchHistoryManager: SearchHistoryManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        // 初始化时监听历史记录
        viewModelScope.launch {
            searchHistoryManager.historyFlow.collect { history ->
                _uiState.update { it.copy(searchHistory = history) }
            }
        }
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                _uiState.update { it.copy(query = event.query) }
                // 仅更新文字，不触发搜索
                if (event.query.isBlank()) {
                     _uiState.update { it.copy(
                        comprehensiveResult = null,
                        listResult = emptyList(),
                        hasSearched = false
                    )}
                }
            }
            is SearchEvent.TabChanged -> {
                if (_uiState.value.selectedTab != event.tab) {
                    _uiState.update { it.copy(selectedTab = event.tab) }
                    if (_uiState.value.query.isNotBlank()) {
                        performSearch()
                    }
                }
            }
            is SearchEvent.Search -> {
                searchJob?.cancel()
                performSearch()
            }
            is SearchEvent.ClearQuery -> {
                _uiState.update { it.copy(
                    query = "",
                    comprehensiveResult = null,
                    listResult = emptyList(),
                    hasSearched = false
                ) }
            }
            is SearchEvent.ClearHistory -> {
                viewModelScope.launch {
                    searchHistoryManager.clearHistory()
                }
            }
        }
    }

    private fun performSearch() {
        val currentState = _uiState.value
        val query = currentState.query
        val tab = currentState.selectedTab

        if (query.isBlank()) return

        // 保存到历史记录
        viewModelScope.launch {
            searchHistoryManager.addSearchTerm(query)
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, hasSearched = true) }

            if (tab == SearchTab.COMPREHENSIVE) {
                when (val result = searchRepository.searchComprehensive(query)) {
                    is Result.Success -> {
                        _uiState.update { it.copy(isLoading = false, comprehensiveResult = result.data) }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            } else {
                _uiState.update { it.copy(listResult = emptyList()) }
                searchByType(query, tab)
            }
        }
    }

    private suspend fun searchByType(query: String, tab: SearchTab) {
        val page = 1
        val size = 50

        when (tab) {
            SearchTab.SONG -> handleResult(searchRepository.searchSongs(query, page, size))
            SearchTab.PLAYLIST -> handleResult(searchRepository.searchPlaylists(query, page, size))
            SearchTab.ALBUM -> handleResult(searchRepository.searchAlbums(query, page, size))
            SearchTab.ARTIST -> handleResult(searchRepository.searchArtists(query, page, size))
            SearchTab.USER -> handleResult(searchRepository.searchUsers(query, page, size))
            else -> {}
        }
    }

    // [Fix] 添加 <T : Any> 约束，确保 List<T> 可以赋值给 List<Any>
    private fun <T : Any> handleResult(result: Result<PageResult<T>>) {
        when (result) {
            is Result.Success -> {
                _uiState.update { it.copy(isLoading = false, listResult = result.data.items) }
            }
            is Result.Error -> {
                _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }
}

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false,
    val selectedTab: SearchTab = SearchTab.COMPREHENSIVE,
    val comprehensiveResult: SearchComprehensiveResult? = null,
    val listResult: List<Any> = emptyList(),
    val searchHistory: List<String> = emptyList() // 添加历史记录状态
)

enum class SearchTab(val title: String) {
    COMPREHENSIVE("综合"),
    SONG("单曲"),
    PLAYLIST("歌单"),
    ALBUM("专辑"),
    ARTIST("歌手"),
    USER("用户")
}

sealed class SearchEvent {
    data class QueryChanged(val query: String) : SearchEvent()
    data class TabChanged(val tab: SearchTab) : SearchEvent()
    object Search : SearchEvent()
    object ClearQuery : SearchEvent()
    object ClearHistory : SearchEvent()
}