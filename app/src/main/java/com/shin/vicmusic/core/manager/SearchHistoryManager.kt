package com.shin.vicmusic.core.manager

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.searchDataStore by preferencesDataStore(name = "search_prefs")

@Singleton
class SearchHistoryManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val HISTORY_KEY = stringPreferencesKey("search_history")

    val historyFlow: Flow<List<String>> = context.searchDataStore.data.map { preferences ->
        val jsonString = preferences[HISTORY_KEY] ?: "[]"
        try {
            Json.decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addSearchTerm(term: String) {
        if (term.isBlank()) return
        context.searchDataStore.edit { preferences ->
            val jsonString = preferences[HISTORY_KEY] ?: "[]"
            val currentList = try {
                Json.decodeFromString<MutableList<String>>(jsonString)
            } catch (e: Exception) {
                mutableListOf()
            }
            
            // 移除已存在的相同搜索词，将其移到最前面
            currentList.remove(term)
            currentList.add(0, term)
            
            // 限制历史记录数量，例如最多 10 条
            if (currentList.size > 10) {
                currentList.removeAt(currentList.lastIndex)
            }
            
            preferences[HISTORY_KEY] = Json.encodeToString(currentList)
        }
    }

    suspend fun clearHistory() {
        context.searchDataStore.edit { it.remove(HISTORY_KEY) }
    }
}