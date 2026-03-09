package com.shin.vicmusic.core.domain.usecase

import com.shin.vicmusic.core.domain.LyricLine
import com.shin.vicmusic.util.LrcHelper
import com.shin.vicmusic.util.ResourceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLyricsUseCase @Inject constructor() {
    suspend operator fun invoke(lyricPath: String): List<LyricLine> = withContext(Dispatchers.IO) {
        if (lyricPath.isEmpty()) return@withContext emptyList()
        try {
            val text = URL(ResourceUtil.r2(lyricPath)).readText()
            LrcHelper.parse(text)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}