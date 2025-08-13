package com.arkhe.sunmi.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.oned.Code128Writer
import com.google.zxing.oned.Code39Writer
import com.google.zxing.oned.EAN13Writer
import com.google.zxing.oned.EAN8Writer
import com.google.zxing.qrcode.QRCodeWriter
import java.io.InputStream

object BitmapUtils {

    /**
     * Create QR Code bitmap using ZXing
     */
    fun createQRCodeBitmap(content: String, size: Int = 200): Bitmap {
        val writer = QRCodeWriter()
        val hints = hashMapOf<EncodeHintType, Any>().apply {
            put(EncodeHintType.MARGIN, 1)
        }

        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height

        val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }

        return bitmap
    }

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

    /**
     * Create text bitmap
     */
    fun createTextBitmap(text: String, textSize: Float = 48f): Bitmap {
        val paint = Paint().apply {
            this.textSize = textSize
            isAntiAlias = true
            color = Color.BLACK
        }

        val textWidth = paint.measureText(text)
        val textHeight = paint.fontMetrics.run { descent - ascent }

        val bitmap = createBitmap(textWidth.toInt() + 20, textHeight.toInt() + 20)

        val canvas = Canvas(bitmap).apply {
            drawColor(Color.WHITE)
            drawText(text, 10f, textHeight - paint.fontMetrics.descent + 10f, paint)
        }

        return bitmap
    }

    /**
     * Load bitmap from assets
     */
    fun loadBitmapFromAssets(context: Context, fileName: String): Bitmap? {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            BitmapFactory.decodeStream(inputStream)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Resize bitmap
     */
    fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val scaleWidth = maxWidth.toFloat() / width
        val scaleHeight = maxHeight.toFloat() / height
        val scale = minOf(scaleWidth, scaleHeight)

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        return bitmap.scale(newWidth, newHeight)
    }
}