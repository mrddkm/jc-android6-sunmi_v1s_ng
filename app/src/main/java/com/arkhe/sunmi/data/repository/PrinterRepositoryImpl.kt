package com.arkhe.sunmi.data.repository

import android.content.Context
import com.arkhe.sunmi.domain.model.BarcodeFormat
import com.arkhe.sunmi.domain.model.ImageAlignment
import com.arkhe.sunmi.domain.model.PaperStatus
import com.arkhe.sunmi.domain.model.PrintData
import com.arkhe.sunmi.domain.model.PrinterStatus
import com.arkhe.sunmi.domain.model.TemperatureStatus
import com.arkhe.sunmi.domain.model.TextAlignment
import com.arkhe.sunmi.domain.repository.PrinterRepository
import com.arkhe.sunmi.utils.BitmapUtils
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterException
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.SunmiPrinterService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrinterRepositoryImpl(
    private val context: Context
) : PrinterRepository {

    private var sunmiService: SunmiPrinterService? = null

    init {
        initializeSunmiService()
    }

    private fun initializeSunmiService() {
        try {
            InnerPrinterManager.getInstance().bindService(
                context,
                object : InnerPrinterCallback() {
                    override fun onConnected(service: SunmiPrinterService?) {
                        sunmiService = service
                    }

                    override fun onDisconnected() {
                        sunmiService = null
                    }
                }
            )
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    override suspend fun print(data: PrintData): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val service = sunmiService ?: return@withContext Result.failure(
                Exception("Printer service not connected")
            )

            when (data) {
                is PrintData.Text -> {
                    service.setAlignment(data.alignment.toSunmiAlignment(), null)
                    if (data.isBold) service.sendRAWData(byteArrayOf(0x1B, 0x45, 0x01), null)
                    service.setFontSize(data.fontSize.toFloat(), null)
                    service.printText(data.content, null)
                    if (data.isBold) service.sendRAWData(byteArrayOf(0x1B, 0x45, 0x00), null)
                }

                is PrintData.Image -> {
                    service.setAlignment(data.alignment.toSunmiAlignment(), null)
                    service.printBitmap(data.bitmap, null)
                }

                is PrintData.QRCode -> {
                    service.setAlignment(1, null) // CENTER
                    service.printQRCode(data.content, data.size, 0, null)
                }

                is PrintData.Barcode -> {
                    service.setAlignment(1, null) // CENTER
                    val bitmap = BitmapUtils.createBarcodeBitmap(
                        data.content,
                        data.format.toZXingFormat(),
                        data.width,
                        data.height
                    )
                    service.printBitmap(bitmap, null)
                }

                is PrintData.LineFeed -> {
                    service.lineWrap(1, null)
                }

                is PrintData.CutPaper -> {
                    service.cutPaper(null)
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
            val service = sunmiService ?: return@withContext Result.failure(
                Exception("Printer service not connected")
            )

            val status = PrinterStatus(
                isConnected = true,
                paperStatus = PaperStatus.OK,
                temperature = TemperatureStatus.NORMAL
            )
            Result.success(status)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun initializePrinter(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val service = sunmiService ?: return@withContext Result.failure(
                Exception("Printer service not connected")
            )

            service.printerInit(null)
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