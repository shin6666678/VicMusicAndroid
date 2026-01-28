package com.shin.vicmusic.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * 将 Uri 对应的文件复制到应用私有缓存目录，并返回绝对路径。
 * 用于图片选择后的临时存储。
 */
fun copyUriToCache(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        // 使用时间戳防止文件名冲突
        val file = File(context.cacheDir, "temp_img_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}