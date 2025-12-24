package com.shin.vicmusic.core.network.datasource

import com.shin.vicmusic.core.data.mapper.toDomain
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.PayType
import com.shin.vicmusic.core.domain.RankListDetail
import com.shin.vicmusic.core.domain.RankListPeak
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.api.RankListPeakDto
import com.shin.vicmusic.core.model.api.SongListItemDto
import com.shin.vicmusic.core.model.response.NetworkPageData
import com.shin.vicmusic.core.model.response.NetworkPageMeta
import com.shin.vicmusic.core.model.response.NetworkResponse
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONGS
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MyMockDatasource @Inject constructor(
) {
    // 模拟后端接口：接收筛选参数
    suspend fun getArtists(
        region: String = "全部",
        type: String = "全部",
        style: String = "全部"
    ): NetworkResponse<NetworkPageData<Artist>> {
        // 模拟数据库中的全量数据
        val allArtists = listOf(
            Artist("1", "周杰伦", "https://example.com/jay.jpg", "华语流行天王", 1000000, true, "港台", "男", "流行"),
            Artist("2", "林俊杰", "https://example.com/jj.jpg", "行走的CD", 800000, false, "港台", "男", "流行"),
            Artist("3", "G.E.M. 邓紫棋", "https://example.com/gem.jpg", "铁肺小天后", 900000, true, "港台", "女", "流行"),
            Artist("4", "薛之谦", "https://example.com/joker.jpg", "深情段子手", 600000, false, "内地", "男", "流行"),
            Artist("5", "Linkin Park", "https://example.com/lp.jpg", "传奇摇滚乐队", 5000000, true, "欧美", "组合", "摇滚"),
            Artist("6", "Taylor Swift", "https://example.com/ts.jpg", "霉霉", 8000000, true, "欧美", "女", "流行"),
            Artist("7", "Eminem", "https://example.com/em.jpg", "说唱之神", 4000000, false, "欧美", "男", "说唱"),
            Artist("8", "BLACKPINK", "https://example.com/bp.jpg", "K-Pop顶流", 3000000, true, "韩国", "组合", "电子"),
            Artist("9", "五月天", "https://example.com/mayday.jpg", "华人第一天团", 2000000, true, "港台", "组合", "摇滚"),
            Artist("10", "周深", "https://example.com/zs.jpg", "天籁之音", 1500000, true, "内地", "男", "国风"),
            Artist("11", "米津玄师", "https://example.com/hachi.jpg", "全能鬼才", 1200000, false, "日本", "男", "流行")
        )

        // 模拟后端的查询过滤逻辑
        val filteredData = allArtists.filter { artist ->
            (region == "全部" || artist.region == region) &&
                    (type == "全部" || artist.type == type) &&
                    (style == "全部" || artist.style == style)
        }


        return NetworkResponse(status = 0, message = "成功", data = NetworkPageData(filteredData))
    }

    fun getArtistById(artistId: String): NetworkResponse<Artist>{
        val artist = when (artistId) {
            "1" -> Artist(
                id = "1",
                name = "周杰伦",
                image = "https://example.com/jay_chou.jpg",
                description = "华语流行乐男歌手、词曲创作人、制作人、MV及电影导演、编剧。",
                followerCount = 1000000,
                isFollowing = true,
                region = "港台",
                type = "男",
                style = "流行"
            )
            else  -> Artist(
                id = "2",
                name = "林俊杰",
                image = "https://example.com/jj_lin.jpg",
            )
         }
        return NetworkResponse(status = 0, message = "成功", data = artist)
    }

    fun getSongsByArtistId(artistId: String): NetworkResponse<List<Song>>{
        val songs = SONGS
        return NetworkResponse(status = 0, message = "成功", data = songs)
    }

    fun getRankListById(id: String): NetworkResponse<RankListDetail> {
        return when (id) {
            "1" -> NetworkResponse(
                status = 0,
                message = "成功",
                data = RankListDetail(
                    id = "1",
                    imageUrl = "https://via.placeholder.com/150/00FF00/FFFFFF?text=Chart3",
                    title = "热歌榜_23首新歌上榜",
                    items = SONGS.subList(0,5)
                )
            )
            else -> NetworkResponse(
                status = 0,
                message = "成功",
                data = RankListDetail(
                    id = "2",
                    imageUrl = "https://via.placeholder.com/150/0000FF/FFFFFF?text=Chart1",
                    title = "巅峰潮流榜_QQ音乐 x 微博",
                    items = SONGS.subList(0,9)
                )
            )
        }

    }

}