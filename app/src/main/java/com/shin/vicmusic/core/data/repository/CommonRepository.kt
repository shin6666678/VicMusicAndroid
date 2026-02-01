package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import java.io.File
import javax.inject.Inject

class CommonRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
) {
    suspend fun uploadImage(file: File?,flag:String): MyNetWorkResult<String> {
        val resp = datasource.uploadImage(file,flag)
        return if (resp.code == 0&&resp.data!=null) MyNetWorkResult.Success(resp.data) else MyNetWorkResult.Error(resp.message ?: "更新失败")
    }
}