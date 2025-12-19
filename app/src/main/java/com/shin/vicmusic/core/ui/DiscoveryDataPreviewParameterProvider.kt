package com.shin.vicmusic.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.shin.vicmusic.core.domain.Album
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.domain.PayType
import com.shin.vicmusic.core.domain.Song
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.SONGS
import com.shin.vicmusic.util.Constant

class DiscoveryPreviewParameterProvider : PreviewParameterProvider<List<Song>> {
    override val values: Sequence<List<Song>> = sequenceOf(SONGS)
}

object DiscoveryPreviewParameterData {
    val ARTIST=Artist(
        id = "1",
        name = "周杰伦",
        image = "https://example.com/jay_chou.jpg",
        description = "华语流行乐男歌手",
        followerCount = 1000000,
        isFollowing = true,
        region = "港台",
        type = "男",
        style = "流行"
    )
    val ARTISTS = listOf(
        Artist(
            id = "1",
            name = "周杰伦",
            image = "https://example.com/jay_chou.jpg",
            description = "华语流行乐男歌手",
            followerCount = 1000000,
            isFollowing = true,
            region = "港台",
            type = "男",
            style = "流行"
        ),
        Artist(
            id = "2",
            name = "林俊杰",
            image = "https://example.com/jj_lin.jpg",
            description = "新加坡籍华语流行音乐男歌手",
            followerCount = 800000,
            isFollowing = false,
            region = "港台",
            type = "男",
            style = "流行"
        ),
        Artist(
            id = "3",
            name = "陈奕迅",
            image = "https://example.com/eason_chan.jpg",
            description = "香港男歌手",
            followerCount = 700000,
            isFollowing = false,
            region = "港台",
            type = "男",
            style = "流行"
        ),
         Artist(
            id = "4",
            name = "薛之谦",
            image = "https://example.com/joker_xue.jpg",
            description = "中国内地流行乐男歌手",
            followerCount = 600000,
            isFollowing = false,
            region = "内地",
            type = "男",
            style = "流行"
        ),
        Artist(
            id = "5",
            name = "王力宏",
            image = "https://example.com/leehom_wang.jpg",
            description = "华语流行乐男歌手",
            followerCount = 500000,
            isFollowing = true,
            region = "港台",
            type = "男",
            style = "流行"
        ),
        Artist(
            id = "6",
            name = "王力宏",
            image = "https://example.com/leehom_wang.jpg",
            description = "华语流行乐男歌手",
            followerCount = 500000,
            isFollowing = true,
            region = "港台",
            type = "男",
            style = "流行"
        ),
        Artist(
            id = "7",
            name = "王力宏",
            image = "https://example.com/leehom_wang.jpg",
            description = "华语流行乐男歌手",
            followerCount = 500000,
            isFollowing = true,
            region = "港台",
            type = "男",
            style = "流行"
        ),
        Artist(
            id = "8",
            name = "王力宏",
            image = "https://example.com/leehom_wang.jpg",
            description = "华语流行乐男歌手",
            followerCount = 500000,
            isFollowing = true,
            region = "港台",
            type = "男",
            style = "流行"
        ),
        Artist(
            id = "9",
            name = "王力宏",
            image = "https://example.com/leehom_wang.jpg",
            description = "华语流行乐男歌手",
            followerCount = 500000,
            isFollowing = true,
            region = "港台",
            type = "男",
            style = "流行"
        ),
    )

    val SONG = Song(
        id = "a9603e83-9b1d-444a-8d7b-402f0672e811",
        title = "City Lights",
        uri = "",
        icon = "neon_dreams_cover.jpg",
        album = Album("1", "Neon Dreams"),
        artist = Artist("1", "SynthWave Explorer", description = "test"),
        payType = PayType.FREE,
        genre = "Synth-Pop",
        lyricStyle = Constant.VALUE0,
        lyric = "Driving down the highway, city lights are fading...",

        )
    val SONGS = listOf(
        // 场景一：典型流行音乐专辑中的主打歌
        SONG,
        // 场景二：古典音乐单曲（ID结构化，lyric字段用于注释）
        Song(
            id = "bwv-1007-1-c1",
            title = "Cello Suite No. 1 in G Major, BWV 1007: I. Prélude",
            uri = "uri1",
            icon = "classical_icon_1.png",
            album = Album("1", "Neon Dreams"),
            artist = Artist("1", "SynthWave Explorer", description = "test"),
            payType = PayType.FREE,
            genre = "Classical",
            lyricStyle = 1, // 假设 1 代表“注释样式”
            lyric = "曲式: A-B-A' 三段体; 情绪: 庄重而平和...",
        ),
        // 场景三：无歌词的电子音乐曲目（过渡曲目，lyric为空字符串）
        Song(
            id = "f6c4d7e0-4c8f-4a0b-9e2c-2a1b0d3c5e7f",
            title = "Stardust Interlude",
            uri = "uri1",
            icon = "", // 使用默认值：无图标
            album = Album("1", "Neon Dreams"),
            artist = Artist("1", "SynthWave Explorer", description = "test"),
            payType = PayType.VIP,
            genre = "Electronic/Ambient",
            lyricStyle = Constant.VALUE0,
            lyric = "",
        ),
        // 额外的测试用例：单曲（trackNumber和totalTrackCount都为1）
        Song(
            id = "s-2024-05-15-a",
            title = "Ephemeral Whisper",
            uri = "uri1", // 模拟流媒体链接
            icon = "ephemeral_cover.png",
            album = Album("1", "Neon Dreams"),
            artist = Artist("1", "SynthWave Explorer", description = "test"),
            payType = PayType.PAY,
            genre = "Indie Pop",
            lyricStyle = Constant.VALUE0,
            lyric = "It vanished as soon as it appeared, like an ephemeral whisper in the wind.",
        ),
        SONG,SONG,SONG,SONG,SONG,SONG,SONG,SONG,SONG,SONG,SONG,SONG,SONG,SONG,SONG,SONG,SONG,SONG
    )
}
