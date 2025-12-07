package com.shin.vicmusic.util

import com.shin.vicmusic.core.config.Config

object ResourceUtil {
    fun r1(uri: String): String {
        return if(uri.startsWith("http")){
            uri
        }else if (uri.startsWith("files")){
            uri
        }else if(uri.startsWith("r/")){
            "${Config.ENDPOINT}${uri}"
        }else String.format(Config.RESOURCE_ENDPOINT,uri)

    }
    fun r2(uri:String):String{
        if(uri.startsWith("http"))
            return uri
        return if (uri.startsWith("files"))
            uri
        else
            String.format(Config.RESOURCE_ENDPOINT2,uri)
    }
}