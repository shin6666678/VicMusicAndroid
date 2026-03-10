package com.shin.vicmusic.core.data.repository

import android.content.Context
import androidx.core.content.edit
import com.shin.vicmusic.core.domain.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

// 定义接口
interface PlayerRepository {
    fun saveLastPlayedSong(song: Song?)
    fun getLastPlayedSong(): Song?
}

// 实现接口
class PlayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PlayerRepository {

    override fun saveLastPlayedSong(song: Song?) {
        val prefs = context.getSharedPreferences("vic_music_prefs", Context.MODE_PRIVATE)
        if (song != null) {
            val json = Json.encodeToString(song)
            prefs.edit { putString("last_song", json) }
        }
    }

    override fun getLastPlayedSong(): Song? {
        val prefs = context.getSharedPreferences("vic_music_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("last_song", null)
        return if (json != null) {
            try {
                Json.decodeFromString<Song>(json)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }
}