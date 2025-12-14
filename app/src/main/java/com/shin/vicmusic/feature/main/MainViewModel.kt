package com.shin.vicmusic.feature.main

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import com.shin.vicmusic.feature.auth.AuthManager
import com.shin.vicmusic.feature.player.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val playerManager: PlayerManager
) : ViewModel() {
    fun playSongAtIndex(index: Int) {
        playerManager.playSongAtIndex(index)
    }
    fun removeSong(index: Int) {
        playerManager.removeSong(index)
    }
    fun togglePlayPause(){
        playerManager.togglePlayPause()
    }

    val currentPlayingSong =playerManager.currentPlayingSong
    val playerState = playerManager.playerState
    val playbackQueue = playerManager.playbackQueue
    val currentQueueIndex = playerManager.currentQueueIndex
}