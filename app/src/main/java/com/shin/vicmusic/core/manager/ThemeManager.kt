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

enum class DressUpStyle(val isDarkTheme: Boolean?) {
    SYSTEM_DEFAULT(null),
    LIGHT_GLOW(false),
    DARK_GLOW(true),
    LIGHT_SOLID(false),
    DARK_SOLID(true),
    LIGHT_NONE(false),
    DARK_NONE(true);

    companion object {
        fun fromOrdinal(ordinal: Int): DressUpStyle {
            return values().getOrNull(ordinal) ?: SYSTEM_DEFAULT
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
