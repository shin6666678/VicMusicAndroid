package com.shin.vicmusic.core.data.repository

import com.shin.vicmusic.core.data.mapper.*
import com.shin.vicmusic.core.domain.*
import com.shin.vicmusic.core.model.api.SearchComprehensiveDto
import com.shin.vicmusic.core.network.retrofit.MyNetworkApiService
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val api: MyNetworkApiService
) {
    // 综合搜索
    suspend fun searchComprehensive(keyword: String): MyNetWorkResult<SearchComprehensiveResult> {
        return try {
            val response = api.searchComprehensive(keyword)
            if (response.code == 0 && response.data != null) {
                MyNetWorkResult.Success(response.data!!.toDomain())
            } else {
                // [Fix] 传入 String
                MyNetWorkResult.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            // [Fix] 传入 String
            MyNetWorkResult.Error(e.message ?: "Network error")
        }
    }

    // 歌曲搜索
    suspend fun searchSongs(keyword: String, page: Int, size: Int = 20): MyNetWorkResult<PageResult<Song>> {
        return try {
            val response = api.searchSongs(keyword = keyword, page = page, size = size)
            if (response.code == 0 && response.data != null) {
                val pageData = response.data!!
                val total = pageData.pagination.total
                // [Fix] 计算 hasMore
                val hasMore = (page * size) < total

                MyNetWorkResult.Success(
                    PageResult(
                        items = pageData.list?.map { it.toDomain() } ?: emptyList(),
                        total = total,
                        page = pageData.pagination.page,
                        hasMore = hasMore
                    )
                )
            } else {
                MyNetWorkResult.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            MyNetWorkResult.Error(e.message ?: "Network error")
        }
    }

    // 歌单搜索
    suspend fun searchPlaylists(keyword: String, page: Int, size: Int = 20): MyNetWorkResult<PageResult<Playlist>> {
        return try {
            val response = api.searchPlaylists(keyword = keyword, page = page, size = size)
            if (response.code == 0 && response.data != null) {
                val pageData = response.data!!
                val total = pageData.pagination.total
                val hasMore = (page * size) < total

                MyNetWorkResult.Success(
                    PageResult(
                        items = pageData.list?.map { it.toDomain() } ?: emptyList(),
                        total = total,
                        page = pageData.pagination.page,
                        hasMore = hasMore
                    )
                )
            } else {
                MyNetWorkResult.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            MyNetWorkResult.Error(e.message ?: "Network error")
        }
    }

    // 专辑搜索
    suspend fun searchAlbums(keyword: String, page: Int, size: Int = 20): MyNetWorkResult<PageResult<Album>> {
        return try {
            val response = api.searchAlbums(keyword = keyword, page = page, size = size)
            if (response.code == 0 && response.data != null) {
                val pageData = response.data!!
                val total = pageData.pagination.total
                val hasMore = (page * size) < total

                MyNetWorkResult.Success(
                    PageResult(
                        items = pageData.list?.map { it.toDomain() } ?: emptyList(),
                        total = total,
                        page = pageData.pagination.page,
                        hasMore = hasMore
                    )
                )
            } else {
                MyNetWorkResult.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            MyNetWorkResult.Error(e.message ?: "Network error")
        }
    }

    // 歌手搜索
    suspend fun searchArtists(keyword: String, page: Int, size: Int = 20): MyNetWorkResult<PageResult<Artist>> {
        return try {
            val response = api.searchArtists(keyword = keyword, page = page, size = size)
            if (response.code == 0 && response.data != null) {
                val pageData = response.data!!
                val total = pageData.pagination.total
                val hasMore = (page * size) < total

                MyNetWorkResult.Success(
                    PageResult(
                        items = pageData.list?.map { it.toDomain() } ?: emptyList(),
                        total = total,
                        page = pageData.pagination.page,
                        hasMore = hasMore
                    )
                )
            } else {
                MyNetWorkResult.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            MyNetWorkResult.Error(e.message ?: "Network error")
        }
    }

    // 用户搜索
    suspend fun searchUsers(keyword: String, page: Int, size: Int = 20): MyNetWorkResult<PageResult<UserInfo>> {
        return try {
            val response = api.searchUsers(keyword = keyword, page = page, size = size)
            if (response.code == 0 && response.data != null) {
                val pageData = response.data!!
                val total = pageData.pagination.total
                val hasMore = (page * size) < total

                MyNetWorkResult.Success(
                    PageResult(
                        items = pageData.list?.map { it.toDomain() } ?: emptyList(),
                        total = total,
                        page = pageData.pagination.page,
                        hasMore = hasMore
                    )
                )
            } else {
                MyNetWorkResult.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            MyNetWorkResult.Error(e.message ?: "Network error")
        }
    }

    // 转换扩展方法
    private fun SearchComprehensiveDto.toDomain(): SearchComprehensiveResult {
        return SearchComprehensiveResult(
            songs = this.songs?.map { it.toDomain() } ?: emptyList(),
            playlists = this.playlists?.map { it.toDomain() } ?: emptyList(),
            albums = this.albums?.map { it.toDomain() } ?: emptyList(),
            artists = this.artists?.map { it.toDomain() } ?: emptyList(),
            users = this.users?.map { it.toDomain() } ?: emptyList()
        )
    }
}