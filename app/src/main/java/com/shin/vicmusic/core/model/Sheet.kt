package com.shin.vicmusic.core.model

import kotlinx.serialization.Serializable
import org.w3c.dom.Comment


@Serializable
data class Sheet (
    val id:String="",
    val title:String="",
    val created:String="",
    val updated:String="",
    //val user:User?=null,
    val icon:String?=null,
    val detail:String?=null,
    val songs:List<Song>?=null,
    val clicksCount:Long=0,
    val collectsCount:Long=0,
    val commentsCount:Long=0,
    val songsCount:Long=0,
    val comments:List<Comment>

)