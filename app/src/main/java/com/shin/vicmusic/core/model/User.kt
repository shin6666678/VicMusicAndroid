package com.shin.vicmusic.core.model

import kotlinx.serialization.Serializable
import java.util.Date


@Serializable
data class User(
    val id: Long?, // 主键ID

    /**
     * 昵称 (Nickname)
     */
    val name: String?,

    /**
     * 密码 (Password)
     */
    val pwd: String?,

    /**
     * 头像 (Avatar)
     */
    val headImg: String?,

    /**
     * 用户签名 (User Signature)
     */
    val slogan: String?,

    /**
     * 0 表示女 (Female)，1 表示男 (Male)
     */
    val sex: Int?,

    /**
     * 积分 (Points)
     */
    val points: Int?,

    val createTime: String,

    /**
     * 邮箱 (Mail)
     */
    val mail: String?
)