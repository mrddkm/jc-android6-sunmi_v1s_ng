package com.arkhe.sunmi.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import com.google.zxing.oned.Code39Writer
import com.google.zxing.oned.EAN13Writer
import com.google.zxing.oned.EAN8Writer

object BitmapUtils {

    /**
     * Create Barcode bitmap using ZXing
     */
    fun createBarcodeBitmap(
        content: String,
        format: BarcodeFormat = BarcodeFormat.CODE_128,
        width: Int = 300,
        height: Int = 100
    ): Bitmap {
        val writer = when (format) {
            BarcodeFormat.CODE_128 -> Code128Writer()
            BarcodeFormat.CODE_39 -> Code39Writer()
            BarcodeFormat.EAN_13 -> EAN13Writer()
            BarcodeFormat.EAN_8 -> EAN8Writer()
            else -> Code128Writer()
        }

        val bitMatrix = writer.encode(content, format, width, height)
        val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }

        return bitmap
    }
}