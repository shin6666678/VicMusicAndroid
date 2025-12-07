package com.shin.vicmusic.core.extension

import android.annotation.SuppressLint
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.clickableNoRipple(
    enabled: Boolean=true,
    onClickLabel:String?=null,
    role: Role?=null,
    onClick:()->Unit
){

}