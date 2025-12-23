package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.model.request.ArtistPageReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.model.response.NetworkResponse
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ArtistRepository@Inject constructor(
    private val datasource: MyRetrofitDatasource
){
    // 透传筛选参数到数据源
    suspend fun getArtists(artistPageReq: ArtistPageReq): NetworkResponse<NetworkPageData<Artist>>{
        val response = datasource.getArtists(artistPageReq)
        return  response

    }

    suspend fun getArtistDetailById(artistId:String) = datasource.getArtistById(artistId)
}
