package com.shin.vicmusic.feature.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.NotifyRepository
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.model.api.NotifyDto
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.feature.common.bar.CommonTopBar
import com.shin.vicmusic.feature.common.item.ItemNotify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedNotifyUiState(
    val isLoading: Boolean = false,
    val items: List<NotifyDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class FeedNotifyViewModel @Inject constructor(
    private val notifyRepository: NotifyRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FeedNotifyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadFeedNotifications()
    }

    fun loadFeedNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = notifyRepository.getFeedNotifyPage(1, 100)
            if (result is MyNetWorkResult.Success<NetworkPageData<NotifyDto>>) {
                _uiState.update { it.copy(items = result.data.list ?: emptyList()) }
            } else if (result is MyNetWorkResult.Error) {
                _uiState.update { it.copy(error = result.message) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

@Composable
fun FeedNotifyRoute(
    viewModel: FeedNotifyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navController = LocalNavController.current

    Scaffold(
        topBar = {
            CommonTopBar(
                midText = "动态消息",
                popBackStack = navController::popBackStack
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.items) { notify ->
                    ItemNotify(notify)
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
