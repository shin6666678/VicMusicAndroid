package com.shin.vicmusic.core.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromOrdinal(ordinal: Int): AppThemeMode {
            return values().getOrNull(ordinal) ?: SYSTEM
        }
    }
}

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val THEME_KEY = intPreferencesKey("theme_mode")

    /**
     * 获取当前的主题设置流
     */
    val themeMode: Flow<AppThemeMode> = context.themeDataStore.data.map { preferences ->
        val ordinal = preferences[THEME_KEY] ?: AppThemeMode.SYSTEM.ordinal
        AppThemeMode.fromOrdinal(ordinal)
    }

    /**
     * 存储用户选择的主题模式
     */
    suspend fun setThemeMode(mode: AppThemeMode) {
        context.themeDataStore.edit { preferences ->
            preferences[THEME_KEY] = mode.ordinal
        }
    }
}
