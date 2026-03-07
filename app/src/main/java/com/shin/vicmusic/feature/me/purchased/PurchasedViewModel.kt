package com.shin.vicmusic.feature.me.purchased

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PurchasedViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(PurchasedState())
    val state: StateFlow<PurchasedState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PurchasedEffect>()
    val effect: SharedFlow<PurchasedEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: PurchasedIntent) {
        when (intent) {
            is PurchasedIntent.LoadData -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            _state.update { it.copy(isLoading = false) }
        }
    }
}
