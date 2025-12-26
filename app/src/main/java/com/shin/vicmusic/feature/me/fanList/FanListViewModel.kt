package com.shin.vicmusic.feature.me.fanList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.ArtistRepository
import com.shin.vicmusic.core.data.repository.UserRepository
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FanListViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    var userList by mutableStateOf<List<UserInfo>>(emptyList())
    var artistList by mutableStateOf<List<Artist>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun loadData() {
        viewModelScope.launch {
            isLoading = true
                val res = userRepository.getFans()
                if (res is Result.Success) userList = res.data
            isLoading = false
        }
    }
}