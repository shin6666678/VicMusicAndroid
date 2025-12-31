package com.shin.vicmusic.feature.relationship.followList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.RelationshipRepository
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowListViewModel @Inject constructor(
    private val relationshipRepository: RelationshipRepository
) : ViewModel() {

    var tabIndex by mutableIntStateOf(0)
    var userList by mutableStateOf<List<UserInfo>>(emptyList())
    var artistList by mutableStateOf<List<Artist>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun loadData() {
        viewModelScope.launch {
            isLoading = true
            if (tabIndex == 0) {
                val res = relationshipRepository.getFollowedUser(0,99)
                if (res is Result.Success) userList = res.data.items
            } else {
                val res = relationshipRepository.getFollowedArtists(0,99)
                if (res is Result.Success) artistList = res.data.items
            }
            isLoading = false
        }
    }

    fun switchTab(index: Int) {
        tabIndex = index
        loadData()
    }
}