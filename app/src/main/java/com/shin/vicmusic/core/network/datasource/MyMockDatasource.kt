package com.shin.vicmusic.core.network.datasource

import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.model.response.NetworkResponse
import com.shin.vicmusic.core.network.retrofit.MyNetworkApiService
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONGS
import javax.inject.Inject
import javax.inject.Singleton

data class RankPageData(
    val recommends: List<RankRecommendDto>,
    val peaks: List<RankPeakDto>
)
data class RankRecommendDto(val title: String, val color: Long)
data class RankPeakDto(val title: String, val img: String, val top3: List<RankSongDto>)
data class RankSongDto(val name: String, val artist: String)

@Singleton
class MyMockDatasource @Inject constructor(
) {
    // 模拟后端接口：接收筛选参数
    suspend fun getArtists(
        region: String = "全部",
        type: String = "全部",
        style: String = "全部"
    ): NetworkResponse<List<Artist>> {
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


        return NetworkResponse(status = 0, message = "成功", data = filteredData)
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
    // [新增] 模拟排行榜数据
    suspend fun getRankData(): NetworkResponse<RankPageData> {
        val recommends = listOf(
            RankRecommendDto("腾讯音乐榜", 0xFF42A5F5),
            RankRecommendDto("巅峰潮流榜", 0xFFEF5350),
            RankRecommendDto("韩国Melon榜", 0xFF66BB6A),
            RankRecommendDto("日本Oricon榜", 0xFFFFA726)
        )
        val peaks = listOf(
            RankPeakDto(
                "热歌榜_23首新歌上榜",
                "https://via.placeholder.com/150/00FF00/FFFFFF?text=Chart3",
                listOf(RankSongDto("奇迹航线", "马嘉祺"), RankSongDto("爱错", "王力宏"), RankSongDto("恋人", "李荣浩"))
            ),
            RankPeakDto(
                "巅峰潮流榜_QQ音乐 x 微博",
                "https://via.placeholder.com/150/0000FF/FFFFFF?text=Chart1",
                listOf(RankSongDto("不渝", "梓渝"), RankSongDto("深海漫游指南", "梓渝"), RankSongDto("全世界下雨", "周深"))
            ),
            RankPeakDto(
                "飙升榜_21首新歌上榜",
                "https://via.placeholder.com/150/FF0000/FFFFFF?text=Chart2",
                listOf(RankSongDto("恒星不忘 Forever Forever", "周杰"), RankSongDto("奇迹航线", "马嘉祺"), RankSongDto("爱与欠", "黄子弘凡"))
            )
        )
        return NetworkResponse(status = 0, message = "成功", data = RankPageData(recommends, peaks))
    }

}