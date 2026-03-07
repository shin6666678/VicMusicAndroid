package com.shin.vicmusic.feature.me.dressup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.manager.ThemeManager
import com.shin.vicmusic.core.manager.DressUpStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DressUpViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {

    val currentDressUpStyle: Flow<DressUpStyle> = themeManager.dressUpStyle

    fun updateDressUpStyle(style: DressUpStyle) {
        viewModelScope.launch {
            themeManager.updateDressUpStyle(style)
        }
    }
}
