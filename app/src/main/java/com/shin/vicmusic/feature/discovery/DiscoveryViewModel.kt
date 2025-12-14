package com.shin.vicmusic.feature.discovery

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.domain.Song // [修改] 引用 Domain Model
import com.shin.vicmusic.core.model.User
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONG
import com.shin.vicmusic.feature.auth.AuthManager
import com.shin.vicmusic.feature.player.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val datasource: MyRetrofitDatasource,
    private val authManager: AuthManager,
    private val playerManager: PlayerManager
) : ViewModel(){
    fun addSongToQueue(song: Song){
        playerManager.addSongToQueue(song)
    }
    private val _datum = MutableStateFlow<List<Song>>(emptyList())
    val datum: StateFlow<List<Song>> = _datum

    //直接链接到 AuthViewModel 的 currentUser，实现由于单一数据源(Single Source of Truth)
    // 这样当 AuthViewModel 登录/登出或更新用户信息时，这里会自动同步
    val user: StateFlow<User?> = authManager.currentUser

    init{
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val songs=datasource.songs()
            Log.d(TAG, "Fetched songs: ${songs.data?.list?.size}")
            _datum.value=songs.data?.list?:emptyList()
        }
    }

    // [新增] 处理喜欢/取消喜欢
    fun toggleLike(song: Song) {
        viewModelScope.launch {
            // 1. 发起网络请求
            val response = datasource.likeSong(song.id)

            if (response.status == 0) {
                // 2. 请求成功，更新本地列表状态 (局部刷新)
                _datum.update { list ->
                    list.map { item ->
                        if (item.id == song.id) {
                            item.copy(isLiked = !item.isLiked)
                        } else {
                            item
                        }
                    }
                }
            } else {
                // 处理失败情况，如提示用户
                Log.e(TAG, "Like failed: ${response.message}")
            }
        }
    }

    companion object{
        const val TAG ="DiscoveryViewModel"
    }
}