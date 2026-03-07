package com.shin.vicmusic.feature.me.dressup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.manager.ThemeManager
import com.shin.vicmusic.core.manager.BackgroundStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DressUpViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {

    val currentBackgroundStyle: Flow<BackgroundStyle> = themeManager.backgroundStyle

    fun updateBackgroundStyle(style: BackgroundStyle) {
        viewModelScope.launch {
            themeManager.updateBackgroundStyle(style)
        }
    }
}
