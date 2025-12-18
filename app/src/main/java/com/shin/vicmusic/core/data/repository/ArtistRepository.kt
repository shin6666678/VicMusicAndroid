package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.network.datasource.MyMockDatasource
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ArtistRepository@Inject constructor(
    private val datasource: MyMockDatasource
){
    suspend fun getArtists()=datasource.getArtists()

}
