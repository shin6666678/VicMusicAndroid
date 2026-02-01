package com.shin.vicmusic.core.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.work.HiltWorker
import androidx.media3.common.util.UnstableApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.shin.vicmusic.MainActivity
import com.shin.vicmusic.R
import com.shin.vicmusic.core.data.repository.NotifyRepository
import com.shin.vicmusic.core.domain.MyNetWorkResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// 使用 DataStore 存储通知相关的配置，例如上一次检查到的未读消息数量
private val Context.dataStore by preferencesDataStore(name = "notification_prefs")

/**
 * 后台消息检查 Worker，由 WorkManager 调度。
 * 即使应用退出，系统也会根据配置（如每 15 分钟）自动运行此任务。
 */
@HiltWorker
class MessageCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notifyRepository: NotifyRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "vic_music_message_channel" // 通知渠道 ID
        const val NOTIFICATION_ID = 1001 // 通知实例 ID
        val LAST_UNREAD_COUNT_KEY = intPreferencesKey("last_unread_count") // 存储在 DataStore 中的键名
    }

    /**
     * 执行后台任务的核心逻辑
     */
    override suspend fun doWork(): Result {
        return try {
            Log.d("MessageCheckWorker", "正在检查新私信...")
            // 调用仓库获取最新的未读统计
            val result = notifyRepository.getUnreadCount()
            if (result is MyNetWorkResult.Success) {
                // 获取各项未读数
                val chatUnreadCount = result.data["chat"] ?: 0
                val notifyUnreadCount = result.data["notify"] ?: 0
                // 计算总未读数（私信 + 系统通知）
                val currentTotalUnreadCount = chatUnreadCount + notifyUnreadCount
                
                // 从 DataStore 获取上一次保存的未读数量
                val lastTotalUnreadCount = applicationContext.dataStore.data.map { preferences ->
                    preferences[LAST_UNREAD_COUNT_KEY] ?: 0
                }.first()

                Log.d("MessageCheckWorker", "当前总未读: $currentTotalUnreadCount (私信: $chatUnreadCount, 通知: $notifyUnreadCount), 上次记录: $lastTotalUnreadCount")

                // 逻辑判断：如果有未读且数量比上次记录的要多，说明有新消息，触发系统通知
                if (currentTotalUnreadCount > 0 && currentTotalUnreadCount > lastTotalUnreadCount) {
                    sendNotification(currentTotalUnreadCount)
                }

                // 更新 DataStore 中的记录，确保下次比对正确
                applicationContext.dataStore.edit { preferences ->
                    preferences[LAST_UNREAD_COUNT_KEY] = currentTotalUnreadCount
                }
            }
            Result.success() // 任务成功完成
        } catch (e: Exception) {
            Log.e("MessageCheckWorker", "检查消息失败", e)
            Result.retry() // 发生异常时，由系统安排重试
        }
    }

    @OptIn(UnstableApi::class)
    private fun sendNotification(unreadCount: Int) {
        // Android 13 (API 33) 及以上需要检查通知权限
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("MessageCheckWorker", "缺少通知权限，无法弹出通知")
            return
        }

        // 创建通知渠道（Android 8.0+ 必须）
        createNotificationChannel()

        // 点击通知后的跳转意图，指向 MainActivity
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        // 构建通知内容
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo) // 设置通知小图标
            .setContentTitle("VicMusic") // 通知标题
            .setContentText("您有 $unreadCount 条未读消息") // 通知文本内容
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 设置优先级
            .setContentIntent(pendingIntent) // 点击后的动作
            .setAutoCancel(true) // 点击后消失

        // 发送通知
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }

    /**
     * 创建系统通知渠道，适配 Android 8.0+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "新消息通知"
            val descriptionText = "接收新私信和系统通知"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
