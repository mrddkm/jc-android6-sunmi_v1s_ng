package com.arkhe.sunmi.domain.model

import android.graphics.Bitmap

sealed class PrintData {
    data class Text(
        val content: String,
        val fontSize: Int = 24,
        val isBold: Boolean = false,
        val alignment: TextAlignment = TextAlignment.LEFT
    ) : PrintData()

    data class Image(
        val bitmap: Bitmap,
        val alignment: ImageAlignment = ImageAlignment.CENTER
    ) : PrintData()

    data class QRCode(
        val content: String,
        val size: Int = 200
    ) : PrintData()

    data class Barcode(
        val content: String,
        val format: BarcodeFormat = BarcodeFormat.CODE128,
        val width: Int = 300,
        val height: Int = 100
    ) : PrintData()

    object LineFeed : PrintData()
    object CutPaper : PrintData()
}

enum class TextAlignment { LEFT, CENTER, RIGHT }
enum class ImageAlignment { LEFT, CENTER, RIGHT }
enum class BarcodeFormat { CODE128, CODE39, EAN13, EAN8 }