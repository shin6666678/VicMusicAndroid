package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject

class CommonRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
) {

}