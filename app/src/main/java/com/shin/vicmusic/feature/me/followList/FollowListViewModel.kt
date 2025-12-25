package com.shin.vicmusic.feature.me.followList

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
import com.shin.vicmusic.core.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowListViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val artistRepository: ArtistRepository
) : ViewModel() {

    var tabIndex by mutableIntStateOf(0)
    var userList by mutableStateOf<List<User>>(emptyList())
    var artistList by mutableStateOf<List<Artist>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun loadData() {
        viewModelScope.launch {
            isLoading = true
            if (tabIndex == 0) {
                val res = userRepository.getFollowedUsers()
                if (res is Result.Success) userList = res.data
            } else {
                val res = artistRepository.getFollowedArtists()
                if (res is Result.Success) artistList = res.data
            }
            isLoading = false
        }
    }

    fun switchTab(index: Int) {
        tabIndex = index
        loadData()
    }
}