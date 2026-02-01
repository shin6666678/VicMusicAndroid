package com.shin.vicmusic.feature.relationship.friendList

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.RelationshipRepository
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.domain.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendListViewModel @Inject constructor(
    private val relationshipRepository: RelationshipRepository
) : ViewModel() {

    var userList by mutableStateOf<List<UserInfo>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun loadData() {
        viewModelScope.launch {
            isLoading = true
            val res = relationshipRepository.getFriends(1, 10)
            if (res is MyNetWorkResult.Success) userList = res.data.items
            Log.d("FriendListViewModel", "loadData: $res")
            isLoading = false
        }
    }
}