package com.shin.vicmusic.feature.discovery

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.model.Song
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val datasource: MyRetrofitDatasource // [新增] 注入 API
) : ViewModel(){
    private val _datum = MutableStateFlow<List<Song>>(emptyList())
    val datum: StateFlow<List<Song>> = _datum
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