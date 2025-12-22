package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.network.datasource.MyMockDatasource
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ArtistRepository@Inject constructor(
    private val datasource: MyRetrofitDatasource
){
    // 透传筛选参数到数据源
    suspend fun getArtists(region: String, type: String, style: String) =
        datasource.getArtists(region, type, style)

    suspend fun getArtistDetailById(artistId:String) = datasource.getArtistById(artistId)
}
