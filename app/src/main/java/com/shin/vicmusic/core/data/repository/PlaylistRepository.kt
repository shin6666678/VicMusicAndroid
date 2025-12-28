package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Playlist
import com.shin.vicmusic.core.domain.PlaylistDetail
import com.shin.vicmusic.core.domain.Result
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.network.datasource.MyRetrofitDatasource
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
    private val datasource: MyRetrofitDatasource
) {

    suspend fun getMyPlaylists(): Result<List<Playlist>> {
        val resp = datasource.getMyPlaylists()
        return if (resp.status == 0) {
            val domainList = resp.data?.map { it.toDomain() } ?: emptyList()
            Result.Success(domainList)
        } else {
            Result.Error(resp.message ?: "获取歌单失败")
        }
    }
    suspend fun getPublicPlaylists(): Result<NetworkPageData<Playlist>> {
        val resp = datasource.getPublicPlaylists()
        return if (resp.status == 0 && resp.data != null) {
            val dtoList = resp.data.list ?: emptyList()
            val domainList=dtoList.map { it.toDomain() }
            val domainData=NetworkPageData(
                list = domainList,
                pagination = resp.data.pagination
            )
            Result.Success(domainData)
        } else {
            Result.Error(resp.message ?: "获取歌单失败")
        }
    }

    suspend fun getPlaylistDetail(id: String): Result<PlaylistDetail> {
        val resp = datasource.getPlaylistDetail(id)
        return if (resp.status == 0 && resp.data != null) {
            Result.Success(resp.data.toDomain())
        } else {
            Result.Error(resp.message ?: "获取详情失败")
        }
    }

    suspend fun addSongToPlaylist(playlistId: String, songId: String): Result<Unit> {
        val resp = datasource.addSongToPlaylist(playlistId, songId)
        return if (resp.status == 0) Result.Success(Unit) else Result.Error(
            resp.message ?: "添加失败"
        )
    }

    suspend fun removeSongFromPlaylist(playlistId: String, songId: String): Result<Unit> {
        val resp = datasource.removeSongFromPlaylist(playlistId, songId)
        return if (resp.status == 0) Result.Success(Unit) else Result.Error(
            resp.message ?: "移除失败"
        )
    }

    suspend fun deletePlaylist(id: String): Result<Unit> {
        val resp = datasource.deletePlaylist(id)
        return if (resp.status == 0) Result.Success(Unit) else Result.Error(
            resp.message ?: "删除失败"
        )
    }

    suspend fun addPlaylist(name: String, description: String?, cover: File?): Result<Unit> {
        val resp = datasource.addPlaylist(name, description, cover)
        return if (resp.status == 0) Result.Success(Unit) else Result.Error(resp.message ?: "创建失败")
    }

    suspend fun updatePlaylist(id: String, name: String, description: String?, cover: File?): Result<Unit> {
        val resp = datasource.updatePlaylist(id, name, description, cover)
        return if (resp.status == 0) Result.Success(Unit) else Result.Error(resp.message ?: "更新失败")
    }
}