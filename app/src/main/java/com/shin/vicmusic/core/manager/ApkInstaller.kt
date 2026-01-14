package com.shin.vicmusic.core.manager

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApkInstaller @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val downloadManager by lazy {
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    /**
     * [重构] 执行下载并返回一个进度 Flow
     * @return Flow<Int> - 发射 0-100 的下载进度。如果下载失败，会抛出异常。
     */
    fun downloadAndInstall(url: String, title: String = "应用更新"): Flow<Int> = flow {
        // 1. 准备下载请求
        val fileName = "update.apk"
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(title)
            .setDescription("正在下载新版本...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            .setMimeType("application/vnd.android.package-archive")

        // 2. 开始下载
        val downloadId = downloadManager.enqueue(request)

        var isDownloadFinished = false
        var progress = 0

        // 3. 循环查询下载进度
        while (!isDownloadFinished) {
            val cursor: Cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
            if (cursor.moveToFirst()) {
                val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val status = cursor.getInt(statusIndex)

                when (status) {
                    DownloadManager.STATUS_RUNNING -> {
                        val totalBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                        val downloadedBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                        val totalBytes = cursor.getLong(totalBytesIndex)
                        val downloadedBytes = cursor.getLong(downloadedBytesIndex)
                        if (totalBytes > 0) {
                            progress = ((downloadedBytes * 100) / totalBytes).toInt()
                            emit(progress) // 发射进度
                        }
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        progress = 100
                        emit(progress)
                        isDownloadFinished = true
                        installApk(fileName)
                    }
                    DownloadManager.STATUS_FAILED -> {
                        isDownloadFinished = true
                        throw Exception("下载失败") // 抛出异常，让调用方处理
                    }
                    DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_PENDING -> {
                        // 暂停或等待中，可以根据需要处理，这里暂时不发射新进度
                    }
                }
            }
            cursor.close()
            if (!isDownloadFinished) {
                delay(500) // 每隔 0.5 秒查询一次
            }
        }
    }

    private fun installApk(fileName: String) {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (!file.exists()) return

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}