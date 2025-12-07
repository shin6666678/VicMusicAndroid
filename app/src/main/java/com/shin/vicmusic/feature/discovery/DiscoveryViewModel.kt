package com.shin.vicmusic.feature.discovery

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shin.vicmusic.core.model.Song
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DiscoveryViewModel: ViewModel() {
    private val _datum = MutableStateFlow<List<Song>>(emptyList())
    val datum: StateFlow<List<Song>> = _datum
    init{
        loadData()
    }

    private fun loadData() {
        //_datum.value= DiscoveryPreviewParameterData.SONGS
//        val json = Json.encodeToString(SONG)
//        Log.d(TAG,"encodeToString: $json")
//
//        val obj = Json.decodeFromString<Song>(json)
//        Log.d(TAG,"decodeFromString: $obj")
        viewModelScope.launch {
            val songs=MyRetrofitDatasource.songs()
            _datum.value=songs.data?.list?:emptyList()
        }

    }
    companion object{
        const val TAG ="DiscoveryViewModel"
    }
}