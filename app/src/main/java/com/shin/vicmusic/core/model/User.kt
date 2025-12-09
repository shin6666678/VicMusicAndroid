package com.shin.vicmusic.core.model

import kotlinx.serialization.Serializable
import java.util.Date


@Serializable
data class User(
    val id: Long?=null, // 主键ID

    /**
     * 昵称 (Nickname)
     */
    val name: String?=null,

    /**
     * 密码 (Password)
     */
    val pwd: String?=null,

    /**
     * 头像 (Avatar)
     */
    val headImg: String?=null,

    /**
     * 用户签名 (User Signature)
     */
    val slogan: String?=null,

    /**
     * 0 表示女 (Female)，1 表示男 (Male)
     */
    val sex: Int?=null,

    /**
     * 积分 (Points)
     */
    val points: Int?=null,

    val createTime: String?=null,

    /**
     * 邮箱 (Mail)
     */
    val mail: String?=null
)