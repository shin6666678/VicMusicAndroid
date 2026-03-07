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

enum class DressUpStyle {
    SYSTEM_DEFAULT,
    LIGHT_GLOW,
    DARK_GLOW,
    LIGHT_SOLID,
    DARK_SOLID,
    LIGHT_NONE,
    DARK_NONE,
    RED;

    companion object {
        fun fromOrdinal(ordinal: Int): DressUpStyle {
            return entries.getOrNull(ordinal) ?: SYSTEM_DEFAULT
        }
    }
}

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val DRESS_UP_STYLE_KEY = intPreferencesKey("dress_up_style")

    /**
     * 获取当前的装扮样式流
     */
    val dressUpStyle: Flow<DressUpStyle> = context.themeDataStore.data.map { preferences ->
        val ordinal = preferences[DRESS_UP_STYLE_KEY] ?: DressUpStyle.SYSTEM_DEFAULT.ordinal
        DressUpStyle.fromOrdinal(ordinal)
    }

    /**
     * 存储用户选择的装扮样式
     */
    suspend fun updateDressUpStyle(style: DressUpStyle) {
        context.themeDataStore.edit { preferences ->
            preferences[DRESS_UP_STYLE_KEY] = style.ordinal
        }
    }
}
