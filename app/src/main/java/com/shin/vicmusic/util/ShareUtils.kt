// file: app/src/main/java/com/shin/vicmusic/util/ShareUtils.kt

package com.shin.vicmusic.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.shin.vicmusic.core.domain.Song
import java.io.File
import java.io.FileOutputStream

object ShareUtils {

    /**
     * 触发原生分享菜单，可以分享文本、链接和图片。
     *
     * @param context Context
     * @param song 要分享的歌曲信息
     * @param bitmap 可选的分享图片。如果为 null，则只分享文本和链接。
     * @param appUrl App 的下载或官网链接，作为备用。
     */
    fun shareSong(context: Context, song: Song, bitmap: Bitmap? = null, appUrl: String = "https://yourapp.download.link") {
        // 1. 构建分享的文本内容
        val shareText = "我正在 VicMusic 听 ${song.title} - ${song.artist}，快来一起听！\n\n收听链接：${generateDeepLink(song)} \n或下载 VicMusic：$appUrl"

        val intent = Intent(Intent.ACTION_SEND)

        // 2. 处理分享图片
        if (bitmap != null) {
            // 将 Bitmap 保存到缓存文件，并获取其 Uri
            val imageUri = saveBitmapToCache(context, bitmap)
            if (imageUri != null) {
                intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                intent.type = "image/*" // 设置MIME类型为图片
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                // 图片的标题和文字描述，某些App会使用
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享歌曲：${song.title}")
                intent.putExtra(Intent.EXTRA_TEXT, shareText) // 即使有图片，也附带文本
            } else {
                // 如果图片保存失败，则退回到只分享文本
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, shareText)
            }
        } else {
            // 3. 只分享文本和链接
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, shareText)
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享歌曲：${song.title}")
        }

        // 4. 创建并启动分享选择器
        val chooser = Intent.createChooser(intent, "分享歌曲到...")
        context.startActivity(chooser)
    }

    /**
     * 生成一个指向你App内部歌曲页面的深度链接（Deep Link）。
     * TODO: 你需要配置你的App来响应该链接。
     */
    private fun generateDeepLink(song: Song): String {
        return "vicmusic://song/${song.id}"
    }

    /**
     * 将 Bitmap 保存到应用的缓存目录，并返回一个可供分享的 Content Uri。
     */
    private fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs() // 创建文件夹
            val file = File(cachePath, "shared_song_card.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            // 使用 FileProvider 获取安全的 Uri
            // TODO: 确保在 AndroidManifest.xml 和 file_paths.xml 中正确配置了 FileProvider
            // e.g., authority: "com.shin.vicmusic.fileprovider"
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 你原有的 shareBitmap 方法可以保留或标记为废弃
    @Deprecated("Use shareSong for a more flexible approach", ReplaceWith("shareSong(context, song, bitmap)"))
    fun shareBitmap(context: Context, song: Song, bitmap: Bitmap) {
        shareSong(context, song, bitmap)
    }
}
