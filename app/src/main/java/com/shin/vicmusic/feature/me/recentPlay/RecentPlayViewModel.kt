package com.shin.vicmusic.feature.me.recentPlay

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.HistoryRepository
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentPlayViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    var songList by mutableStateOf<List<Song>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun loadData() {
        viewModelScope.launch {
            isLoading = true
            val res = historyRepository.getHistory()
            if (res is MyNetWorkResult.Success) {
                songList = res.data
            }
            isLoading = false
        }
    }
}