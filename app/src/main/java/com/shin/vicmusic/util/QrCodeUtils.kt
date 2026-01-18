package com.shin.vicmusic.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.EnumMap

object QRCodeUtils {
    /**
     * 生成二维码
     * @param content 内容建议为 H5 落地页 URL
     * @param size 像素尺寸
     */
    fun createQRCode(content: String, size: Int): Bitmap? {
        return try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.MARGIN] = 0 // 去掉二维码本身的白边，方便 UI 控制

            val bitMatrix = QRCodeWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size,
                hints
            )

            val pixels = IntArray(size * size)
            for (y in 0 until size) {
                for (x in 0 until size) {
                    // 使用深色（接近黑色）作为码色，背景为白色
                    pixels[y * size + x] = if (bitMatrix.get(x, y)) 0xFF333333.toInt() else Color.WHITE
                }
            }

            Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, size, 0, 0, size, size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}