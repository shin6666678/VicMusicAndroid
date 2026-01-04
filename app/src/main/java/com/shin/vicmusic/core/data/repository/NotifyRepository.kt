package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.model.api.NotifyDto
import com.shin.vicmusic.core.model.response.NetworkResponse
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject

class NotifyRepository @Inject constructor(
    private val network: MyRetrofitDatasource
) {
    fun getNotifyPage(i: Int, i2: Int) {}

}