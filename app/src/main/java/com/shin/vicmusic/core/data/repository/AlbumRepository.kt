package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.request.AlbumPageReq
import com.shin.vicmusic.core.model.request.SongPageReq
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.model.response.NetworkResponse
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
) {
    suspend fun getAlbums(pageReq: AlbumPageReq)=datasource.getAlbums(pageReq)

    suspend fun getAlbumDetail(albumId: String): NetworkResponse<Album> {
        return datasource.getAlbumById(albumId)
    }

    suspend fun getSongsByAlbumId(pageReq: SongPageReq)= datasource.songs(pageReq)
}