package com.shin.vicmusic.core.domain.usecase

import com.shin.vicmusic.core.data.repository.CommonRepository
import com.shin.vicmusic.core.data.repository.UserRepository
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.manager.AuthManager
import java.io.File
import javax.inject.Inject

class UpdateBgImgUseCase @Inject constructor(
    private val commonRepository: CommonRepository,
    private val userRepository: UserRepository,
    private val authManager: AuthManager
) {
    // 使用 operator invoke 可以让调用像函数一样：updateUserBgUseCase(path)
    suspend operator fun invoke(localPath: String,flag:String): MyNetWorkResult<String> {
        return try {
            val file = File(localPath)
            if (!file.exists()) return MyNetWorkResult.Error("文件不存在")

            // 1. 上传图片
            val uploadResult = commonRepository.uploadImage(file, flag)
            val finalBgUrl = when (uploadResult) {
                is MyNetWorkResult.Success -> uploadResult.data
                is MyNetWorkResult.Error -> return uploadResult
            }

            // 2. 更新数据库/后端
            val updateResult = userRepository.updateUserBgImg(finalBgUrl)
            if (updateResult is MyNetWorkResult.Error) return updateResult

            // 3. 刷新全局内存缓存
            authManager.fetchUserInfo()

            MyNetWorkResult.Success(finalBgUrl)
        } catch (e: Exception) {
            MyNetWorkResult.Error(e.message ?: "操作失败")
        }
    }
}