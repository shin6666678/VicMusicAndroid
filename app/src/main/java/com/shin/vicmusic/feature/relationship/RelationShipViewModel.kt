package com.shin.vicmusic.feature.relationship

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.data.repository.RelationshipRepository
import com.shin.vicmusic.core.domain.MyNetWorkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RelationshipViewModel @Inject constructor(
    private val relationshipRepository: RelationshipRepository
) : ViewModel() {



    fun toggleFollow(targetId: String, targetType: Int) {
        viewModelScope.launch {
            val result = relationshipRepository.follow(targetId, targetType)
            when (result) {
                is MyNetWorkResult.Success -> {
                    // 成功逻辑
                }
                is MyNetWorkResult.Error -> {
                    Log.d("follow", "error")
                }
            }
        }
    }
}

enum class RelationshipTab(val title: String) {
    FOLLOWING("关注"),
    FAN("粉丝"),
    FRIEND("好友"),
}