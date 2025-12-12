package com.shin.vicmusic.feature.discovery

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.model.Song
import com.shin.vicmusic.core.model.User
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONG
import com.shin.vicmusic.feature.auth.AuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val datasource: MyRetrofitDatasource, // [新增] 注入 API
    private val authViewModel: AuthViewModel // [修改] 注入 AuthViewModel
) : ViewModel(){
    private val _datum = MutableStateFlow<List<Song>>(emptyList())
    val datum: StateFlow<List<Song>> = _datum

    // [修改] 直接链接到 AuthViewModel 的 currentUser，实现由于单一数据源(Single Source of Truth)
    // 这样当 AuthViewModel 登录/登出或更新用户信息时，这里会自动同步
    val user: StateFlow<User?> = authViewModel.currentUser

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
    companion object{
        const val TAG ="DiscoveryViewModel"
    }
}