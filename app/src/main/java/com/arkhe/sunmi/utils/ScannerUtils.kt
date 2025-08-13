package com.arkhe.sunmi.utils

import com.google.zxing.BarcodeFormat

object ScannerUtils {

    val supportedFormats = listOf(
        BarcodeFormat.QR_CODE,
        BarcodeFormat.CODE_128,
        BarcodeFormat.CODE_39,
        BarcodeFormat.EAN_13,
        BarcodeFormat.EAN_8,
        BarcodeFormat.UPC_A,
        BarcodeFormat.UPC_E
    )

    /**
     * Validate scan result content
     */
    fun validateScanContent(content: String, format: String): Boolean {
        return when (format) {
            "QR_CODE" -> content.isNotBlank()
            "CODE_128", "CODE_39" -> content.isValidBarcode()
            "EAN_13" -> content.length == 13 && content.all { it.isDigit() }
            "EAN_8" -> content.length == 8 && content.all { it.isDigit() }
            "UPC_A" -> content.length == 12 && content.all { it.isDigit() }
            "UPC_E" -> content.length == 8 && content.all { it.isDigit() }
            else -> true
        }
    }

    /**
     * Format scan result for display
     */
    fun formatScanResult(content: String, format: String): String {
        return when (format) {
            "QR_CODE" -> "QR: $content"
            "CODE_128" -> "Code128: $content"
            "CODE_39" -> "Code39: $content"
            "EAN_13" -> "EAN13: $content"
            "EAN_8" -> "EAN8: $content"
            "UPC_A" -> "UPC-A: $content"
            "UPC_E" -> "UPC-E: $content"
            else -> "$format: $content"
        }
    }

    /**
     * Get format description
     */
    fun getFormatDescription(format: String): String {
        return when (format) {
            "QR_CODE" -> "QR Code"
            "CODE_128" -> "Code 128"
            "CODE_39" -> "Code 39"
            "EAN_13" -> "EAN-13"
            "EAN_8" -> "EAN-8"
            "UPC_A" -> "UPC-A"
            "UPC_E" -> "UPC-E"
            else -> format
        }
    }
}