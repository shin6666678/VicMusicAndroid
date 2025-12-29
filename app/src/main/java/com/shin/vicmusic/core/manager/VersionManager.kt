package com.shin.vicmusic.core.manager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VersionManager @Inject constructor(
    private val dataStore: DataStore<Preferences> // 改为直接注入
) {
    private val VERSION_CODE_KEY = intPreferencesKey("last_run_version_code")

    // 获取保存的版本号，默认为 -1
    suspend fun getSavedVersionCode(): Int {
        return dataStore.data.map { preferences ->
            preferences[VERSION_CODE_KEY] ?: -1
        }.first()
    }

    // 保存当前版本号
    suspend fun saveVersionCode(code: Int) {
        dataStore.edit { preferences ->
            preferences[VERSION_CODE_KEY] = code
        }
    }
}