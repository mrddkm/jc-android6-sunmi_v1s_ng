package com.arkhe.sunmi.utils

import android.annotation.SuppressLint
import com.arkhe.sunmi.domain.model.PrintData


object PrinterUtils {
    /**
     * Create a standard receipt template
     */
    @SuppressLint("DefaultLocale")
    fun createReceiptTemplate(
        storeName: String,
        items: List<ReceiptItem>,
        total: Double,
        qrContent: String? = null
    ): List<PrintData> {
        val receiptItems = mutableListOf<PrintData>()

        // Header
        receiptItems.add(
            PrintData.Text(
                content = storeName,
                fontSize = 28,
                isBold = true,
                alignment = com.arkhe.sunmi.domain.model.TextAlignment.CENTER,
            )
        )
        receiptItems.add(PrintData.LineFeed)

        // Date and time
        val dateTime = java.text.SimpleDateFormat(
            "dd/MM/yyyy HH:mm:ss",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

        receiptItems.add(PrintData.Text("Date: $dateTime"))
        receiptItems.add(PrintData.Text("--------------------------------"))

        // Items
        items.forEach { item ->
            receiptItems.add(
                PrintData.Text("${item.name.padEnd(20)} ${String.format("%.2f", item.price)}")
            )
        }

        receiptItems.add(PrintData.Text("--------------------------------"))
        receiptItems.add(
            PrintData.Text(
                content = "TOTAL: ${String.format("%.2f", total)}",
                isBold = true
            )
        )
        receiptItems.add(PrintData.LineFeed)

        // QR Code if provided
        qrContent?.let {
            receiptItems.add(PrintData.QRCode(it))
            receiptItems.add(PrintData.LineFeed)
        }

        // Cut paper
        receiptItems.add(PrintData.CutPaper)

        return receiptItems
    }

    /**
     * Validate print content
     */
    fun validatePrintContent(data: PrintData): Boolean {
        return when (data) {
            is PrintData.Text -> data.content.isNotBlank()
            is PrintData.QRCode -> data.content.isNotBlank() && data.content.length <= 2000
            is PrintData.Barcode -> data.content.isValidBarcode()
            is PrintData.Image -> data.bitmap.isValidForPrint()
            else -> true
        }
    }
}

data class ReceiptItem(
    val name: String,
    val price: Double,
    val quantity: Int = 1
)