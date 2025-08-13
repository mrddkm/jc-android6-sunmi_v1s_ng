package com.arkhe.sunmi.data.repository

import android.content.Context
import com.arkhe.sunmi.domain.model.*
import com.arkhe.sunmi.domain.repository.PrinterRepository
import com.arkhe.sunmi.utils.BitmapUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrinterRepositoryImpl(
    private val context: Context
) : PrinterRepository {

    private val sunmiPrintHelper = SunmiPrintHelper.getInstance()

    override suspend fun print(data: PrintData): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            when (data) {
                is PrintData.Text -> {
                    sunmiPrintHelper.setAlign(data.alignment.toSunmiAlignment())
                    if (data.isBold) sunmiPrintHelper.setBold(true)
                    sunmiPrintHelper.setFontSize(data.fontSize.toFloat())
                    sunmiPrintHelper.printText(data.content, null)
                    if (data.isBold) sunmiPrintHelper.setBold(false)
                }

                is PrintData.Image -> {
                    sunmiPrintHelper.setAlign(data.alignment.toSunmiAlignment())
                    sunmiPrintHelper.printBitmap(data.bitmap, null)
                }

                is PrintData.QRCode -> {
                    sunmiPrintHelper.setAlign(1) // CENTER
                    sunmiPrintHelper.printQr(data.content, data.size, null)
                }

                is PrintData.Barcode -> {
                    sunmiPrintHelper.setAlign(1) // CENTER
                    val bitmap = BitmapUtils.createBarcodeBitmap(
                        data.content,
                        data.format.toZXingFormat(),
                        data.width,
                        data.height
                    )
                    sunmiPrintHelper.printBitmap(bitmap, null)
                }

                is PrintData.LineFeed -> {
                    sunmiPrintHelper.feedPaper()
                }

                is PrintData.CutPaper -> {
                    sunmiPrintHelper.cutpaper(null)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun printReceipt(items: List<PrintData>): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                items.forEach { item ->
                    print(item).getOrThrow()
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun checkPrinterStatus(): Result<PrinterStatus> = withContext(Dispatchers.IO) {
        try {
            val status = PrinterStatus(
                isConnected = sunmiPrintHelper.printerStatus != null,
                paperStatus = PaperStatus.OK, // You can implement actual status check
                temperature = TemperatureStatus.NORMAL
            )
            Result.success(status)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun initializePrinter(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            sunmiPrintHelper.initPrinter(null)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun TextAlignment.toSunmiAlignment(): Int = when (this) {
        TextAlignment.LEFT -> 0
        TextAlignment.CENTER -> 1
        TextAlignment.RIGHT -> 2
    }

    private fun ImageAlignment.toSunmiAlignment(): Int = when (this) {
        ImageAlignment.LEFT -> 0
        ImageAlignment.CENTER -> 1
        ImageAlignment.RIGHT -> 2
    }

    private fun BarcodeFormat.toZXingFormat(): com.google.zxing.BarcodeFormat = when (this) {
        BarcodeFormat.CODE128 -> com.google.zxing.BarcodeFormat.CODE_128
        BarcodeFormat.CODE39 -> com.google.zxing.BarcodeFormat.CODE_39
        BarcodeFormat.EAN13 -> com.google.zxing.BarcodeFormat.EAN_13
        BarcodeFormat.EAN8 -> com.google.zxing.BarcodeFormat.EAN_8
    }
}