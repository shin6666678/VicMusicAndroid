package com.shin.vicmusic.util
import com.shin.vicmusic.core.domain.LyricLine

object LrcHelper {
    fun parse(content: String): List<LyricLine> {
        return content.lineSequence().mapNotNull { line ->
            val right = line.indexOf(']')
            if (line.startsWith("[") && right > 1) {
                val parts = line.substring(1, right).split(":", ".")
                val min = parts[0].toLongOrNull() ?: 0L
                val sec = parts[1].toLongOrNull() ?: 0L
                val ms = parts.getOrNull(2)?.let { if (it.length < 3) it.toLong() * 10 else it.toLong() } ?: 0L
                LyricLine(min * 60000 + sec * 1000 + ms, line.substring(right + 1).trim())
            } else null
        }.toList().sortedBy { it.time }
    }
}