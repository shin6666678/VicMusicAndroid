package com.shin.vicmusic.feature.relationship.fanList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.util.CoilUtils.result
import com.shin.vicmusic.core.data.repository.RelationshipRepository
import com.shin.vicmusic.core.data.repository.UserRepository
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.domain.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FanListViewModel @Inject constructor(
    private val relationshipRepository: RelationshipRepository
) : ViewModel() {

    var userList by mutableStateOf<List<UserInfo>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun loadData() {
        viewModelScope.launch {
            isLoading = true
                val res = relationshipRepository.getFans(1, 10)
                if (res is Result.Success) userList = res.data.items
            isLoading = false
        }
    }
}