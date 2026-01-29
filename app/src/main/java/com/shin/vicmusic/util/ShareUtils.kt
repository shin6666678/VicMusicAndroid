package com.shin.vicmusic.util

import android.content.ClipData // ‼️ 导入 ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build // ‼️ 导入 Build
import androidx.core.content.FileProvider
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.Song
import java.io.File
import java.io.FileOutputStream

object ShareUtils {

    fun shareSong(context: Context, song: Song, bitmap: Bitmap? = null, appUrl: String = "https://yourapp.download.link") {
        val shareText = "我正在 VicMusic 听 ${song.title} - ${song.artist.name}，快来一起听！\n\n收听链接：${generateDeepLink(song)} \n或下载 VicMusic：$appUrl"
        val intent = Intent(Intent.ACTION_SEND)

        var imageUri: Uri? = null

        if (bitmap != null) {
            imageUri = saveBitmapToCache(context, bitmap)
            if (imageUri != null) {
                intent.type = "image/png" // 可以更精确地指定为 png
                intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                // ‼️【核心修复 1】为 ClipData 设置 URI。这是现代 Android 系统处理权限的首选方式
                intent.clipData = ClipData.newUri(context.contentResolver, "Image", imageUri)
                // ‼️【核心修复 2】同时添加读和写的权限（虽然主要需要读，但写权限在某些设备上能解决兼容性问题）
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            } else {
                intent.type = "text/plain"
            }
        } else {
            intent.type = "text/plain"
        }

        intent.putExtra(Intent.EXTRA_TEXT, shareText)
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享歌曲：${song.title}")

        val chooser = Intent.createChooser(intent, "分享歌曲到...")

        // ‼️【核心修复 3】手动遍历所有能处理分享的应用，并为它们一一授权
        // 这是解决权限问题的最稳妥方法
        val resInfoList = context.packageManager.queryIntentActivities(chooser, 0)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            if (imageUri != null) {
                context.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        }

        // 【可选，但推荐】确保 chooser 本身也带有 FLAG_ACTIVITY_NEW_TASK
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooser)
    }

    fun sharePlaylist(context: Context, playlist: Playlist, bitmap: Bitmap? = null, appUrl: String = "https://yourapp.download.link") {
        val shareText = "我发现了一个很棒的歌单《${playlist.name}》by ${playlist.ownerName}，内含 ${playlist.songCount} 首歌曲！\n\n查看详情：${generateDeepLink(playlist)} \n或下载 VicMusic：$appUrl"
        val intent = Intent(Intent.ACTION_SEND)

        var imageUri: Uri? = null

        if (bitmap != null) {
            imageUri = saveBitmapToCache(context, bitmap)
            if (imageUri != null) {
                intent.type = "image/png"
                intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                intent.clipData = ClipData.newUri(context.contentResolver, "Image", imageUri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            } else {
                intent.type = "text/plain"
            }
        } else {
            intent.type = "text/plain"
        }

        intent.putExtra(Intent.EXTRA_TEXT, shareText)
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享歌单：${playlist.name}")

        val chooser = Intent.createChooser(intent, "分享歌单到...")
        val resInfoList = context.packageManager.queryIntentActivities(chooser, 0)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            if (imageUri != null) {
                context.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        }
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun shareAlbum(context: Context, album: Album, bitmap: Bitmap? = null, appUrl: String = "https://yourapp.download.link") {
        val shareText = "推荐轻听 ${album.artist.name} 的专辑《${album.title}》，好歌都在这里！\n\n查看详情：${generateDeepLink(album)} \n或下载 VicMusic：$appUrl"
        val intent = Intent(Intent.ACTION_SEND)

        var imageUri: Uri? = null

        if (bitmap != null) {
            imageUri = saveBitmapToCache(context, bitmap)
            if (imageUri != null) {
                 intent.type = "image/png"
                 intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                 intent.clipData = ClipData.newUri(context.contentResolver, "Image", imageUri)
                 intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            } else {
                 intent.type = "text/plain"
            }
        } else {
             intent.type = "text/plain"
        }

        intent.putExtra(Intent.EXTRA_TEXT, shareText)
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享专辑：${album.title}")

        val chooser = Intent.createChooser(intent, "分享专辑到...")
        val resInfoList = context.packageManager.queryIntentActivities(chooser, 0)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            if (imageUri != null) {
                context.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        }
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun generateDeepLink(song: Song): String {
        return "vicmusic://song/${song.id}"
    }

    private fun generateDeepLink(playlist: Playlist): String {
        return "vicmusic://playlist/${playlist.id}"
    }

    private fun generateDeepLink(album: Album): String {
        return "vicmusic://album/${album.id}"
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "shared_song_card.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
