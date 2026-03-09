package com.shin.vicmusic.core.data.repository

import android.R.attr.name
import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.PlaylistDetail
import com.shin.vicmusic.core.domain.MyNetWorkResult
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
) {

    suspend fun getMyPlaylists(): MyNetWorkResult<List<Playlist>> {
        val resp = datasource.getMyPlaylists()
        return if (resp.code == 0) {
            val domainList = resp.data?.map { it.toDomain() } ?: emptyList()
            MyNetWorkResult.Success(domainList)
        } else {
            MyNetWorkResult.Error(resp.message ?: "获取歌单失败")
        }
    }

    suspend fun getPublicPlaylists(): MyNetWorkResult<NetworkPageData<Playlist>> {
        val resp = datasource.getPublicPlaylists()
        return if (resp.code == 0 && resp.data != null) {
            val dtoList = resp.data.list ?: emptyList()
            val domainList = dtoList.map { it.toDomain() }
            val domainData = NetworkPageData(
                list = domainList,
                pagination = resp.data.pagination
            )
            MyNetWorkResult.Success(domainData)
        } else {
            MyNetWorkResult.Error(resp.message ?: "获取歌单失败")
        }
    }

    suspend fun getPlaylistDetail(id: String): MyNetWorkResult<PlaylistDetail> {
        val resp = datasource.getPlaylistDetail(id)
        return if (resp.code == 0 && resp.data != null) {
            MyNetWorkResult.Success(resp.data.toDomain())
        } else {
            MyNetWorkResult.Error(resp.message ?: "获取详情失败")
        }
    }

    suspend fun addSongToPlaylist(playlistId: String, songId: String): MyNetWorkResult<Unit> {
        val resp = datasource.addSongToPlaylist(playlistId, songId)
        return if (resp.code == 0) MyNetWorkResult.Success(Unit) else MyNetWorkResult.Error(
            resp.message ?: "添加失败"
        )
    }

    suspend fun removeSongFromPlaylist(playlistId: String, songId: String): MyNetWorkResult<Unit> {
        val resp = datasource.removeSongFromPlaylist(playlistId, songId)
        return if (resp.code == 0) MyNetWorkResult.Success(Unit) else MyNetWorkResult.Error(
            resp.message ?: "移除失败"
        )
    }

    suspend fun deletePlaylist(id: String): MyNetWorkResult<Unit> {
        val resp = datasource.deletePlaylist(id)
        return if (resp.code == 0) MyNetWorkResult.Success(Unit) else MyNetWorkResult.Error(
            resp.message ?: "删除失败"
        )
    }

    suspend fun addPlaylist(
        name: String,
        description: String?,
        cover: File?
    ): MyNetWorkResult<Unit> {
        val resp = datasource.addPlaylist(name, description, cover)
        return if (resp.code == 0) MyNetWorkResult.Success(Unit) else MyNetWorkResult.Error(
            resp.message ?: "创建失败"
        )
    }

    suspend fun updatePlaylist(
        id: String,
        name: String?,
        description: String?,
        cover: String?,
        isPublic: Int?
    ): MyNetWorkResult<Unit> {
        val resp = datasource.updatePlaylist(id, name, description, cover, isPublic)
        return if (resp.code == 0) MyNetWorkResult.Success(Unit) else MyNetWorkResult.Error(
            resp.message ?: "更新失败"
        )
    }

}

