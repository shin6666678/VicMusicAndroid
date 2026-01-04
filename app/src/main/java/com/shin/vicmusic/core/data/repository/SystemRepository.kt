package com.shin.vicmusic.core.data.repository

import android.content.Context
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.model.api.AppUpdateDto
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource, // 假设你有Datasource封装，或者直接用Api
    @ApplicationContext private val context: Context
) {
    // 获取当前APP版本号
    fun getCurrentVersionCode(): Int {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                pInfo.versionCode
            }
        } catch (e: Exception) {
            0
        }
    }

    suspend fun checkAppUpdate(): Result<AppUpdateDto> {
        val currentCode = getCurrentVersionCode()
        // 调用 Datasource
        val resp = datasource.checkUpdate(currentCode)
        return if (resp.code == 0 && resp.data != null) {
            Result.Success(resp.data)
        } else {
            Result.Error(resp.message ?: "Check update failed")
        }
    }

    suspend fun getGlobalUnreadCount(): Int {
        val response = datasource.getUnreadCount()
        if (response.code == 0 && response.data != null) {
            return response.data["total"] ?: 0
        }
        return 0
    }
}