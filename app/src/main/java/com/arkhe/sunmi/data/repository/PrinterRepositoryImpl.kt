package com.arkhe.sunmi.data.repository

import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class PrinterRepositoryImpl(
    private val context: Context
) : PrinterRepository {

    companion object {
        private const val TAG = "PrinterRepository"
        private const val CONNECTION_TIMEOUT = 5000L
    }

    private var sunmiService: SunmiPrinterService? = null
    private var isServiceConnected = false

    init {
        Log.d(TAG, "PrinterRepositoryImpl initialized")
        initializeSunmiService()
    }

    private fun initializeSunmiService() {
        Log.d(TAG, "Initializing Sunmi printer service")

        try {
            InnerPrinterManager.getInstance().bindService(
                context,
                object : InnerPrinterCallback() {
                    override fun onConnected(service: SunmiPrinterService?) {
                        Log.d(TAG, "Printer service connected: ${service != null}")
                        sunmiService = service
                        isServiceConnected = service != null

                        if (service != null) {
                            try {
                                // Initialize printer when service is connected
                                service.printerInit(null)
                                Log.d(TAG, "Printer initialized successfully")
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to initialize printer", e)
                            }
                        }
                    }

                    override fun onDisconnected() {
                        Log.d(TAG, "Printer service disconnected")
                        sunmiService = null
                        isServiceConnected = false
                    }
                }
            )
        } catch (e: InnerPrinterException) {
            Log.e(TAG, "Failed to bind printer service", e)
            isServiceConnected = false
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error binding printer service", e)
            isServiceConnected = false
        }
    }

    private suspend fun waitForServiceConnection(): Boolean {
        Log.d(TAG, "Waiting for service connection...")

        return withTimeoutOrNull(CONNECTION_TIMEOUT) {
            var attempts = 0
            while (!isServiceConnected && sunmiService == null && attempts < 50) {
                delay(100)
                attempts++
                Log.v(TAG, "Waiting for connection... attempt $attempts")
            }
            isServiceConnected && sunmiService != null
        } ?: false
    }

    override suspend fun print(data: PrintData): Result<Unit> = withContext(Dispatchers.IO) {
        Log.d(TAG, "print() called with data type: ${data::class.simpleName}")

        try {
            // Ensure service is connected
            if (!isServiceConnected || sunmiService == null) {
                Log.w(TAG, "Service not connected, attempting to connect...")
                initializeSunmiService()

                if (!waitForServiceConnection()) {
                    val error = "Printer service not available after timeout"
                    Log.e(TAG, error)
                    return@withContext Result.failure(Exception(error))
                }
            }

            val service = sunmiService ?: run {
                val error = "Printer service is null"
                Log.e(TAG, error)
                return@withContext Result.failure(Exception(error))
            }

            Log.d(TAG, "Processing print data: ${data::class.simpleName}")

            when (data) {
                is PrintData.Text -> {
                    Log.d(TAG, "Printing text: '${data.content}' with alignment: ${data.alignment}")
                    service.setAlignment(data.alignment.toSunmiAlignment(), null)

                    if (data.isBold) {
                        Log.d(TAG, "Setting bold text")
                        service.sendRAWData(byteArrayOf(0x1B, 0x45, 0x01), null)
                    }

                    service.setFontSize(data.fontSize.toFloat(), null)
                    service.printText(data.content, null)

                    if (data.isBold) {
                        service.sendRAWData(byteArrayOf(0x1B, 0x45, 0x00), null)
                    }
                }

                is PrintData.Image -> {
                    Log.d(TAG, "Printing image with alignment: ${data.alignment}")
                    service.setAlignment(data.alignment.toSunmiAlignment(), null)
                    service.printBitmap(data.bitmap, null)
                }

                is PrintData.QRCode -> {
                    Log.d(TAG, "Printing QR code: '${data.content}' with size: ${data.size}")
                    service.setAlignment(1, null) // CENTER
                    service.printQRCode(data.content, data.size, 0, null)
                }

                is PrintData.Barcode -> {
                    Log.d(TAG, "Printing barcode: '${data.content}' format: ${data.format}")
                    service.setAlignment(1, null) // CENTER

                    try {
                        val bitmap = BitmapUtils.createBarcodeBitmap(
                            data.content,
                            data.format.toZXingFormat(),
                            data.width,
                            data.height
                        )
                        service.printBitmap(bitmap, null)
                        Log.d(TAG, "Barcode bitmap created and sent to printer")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to create barcode bitmap", e)
                        throw e
                    }
                }

                is PrintData.LineFeed -> {
                    Log.d(TAG, "Printing line feed")
                    service.lineWrap(1, null)
                }

                is PrintData.CutPaper -> {
                    Log.d(TAG, "Cutting paper")
                    service.cutPaper(null)
                }
            }

            Log.d(TAG, "Print operation completed successfully")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Print operation failed", e)
            Result.failure(e)
        }
    }

    override suspend fun printReceipt(items: List<PrintData>): Result<Unit> =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "printReceipt() called with ${items.size} items")

            try {
                items.forEachIndexed { index, item ->
                    Log.d(TAG, "Printing receipt item $index: ${item::class.simpleName}")
                    print(item).getOrThrow()
                }

                Log.d(TAG, "Receipt printed successfully")
                Result.success(Unit)

            } catch (e: Exception) {
                Log.e(TAG, "Receipt printing failed", e)
                Result.failure(e)
            }
        }

    override suspend fun checkPrinterStatus(): Result<PrinterStatus> = withContext(Dispatchers.IO) {
        Log.d(TAG, "checkPrinterStatus() called")

        try {
            if (!isServiceConnected || sunmiService == null) {
                Log.w(TAG, "Service not connected for status check")

                val status = PrinterStatus(
                    isConnected = false,
                    paperStatus = PaperStatus.OK,
                    temperature = TemperatureStatus.NORMAL
                )
                return@withContext Result.success(status)
            }

            val service = sunmiService!!

            // You can add more sophisticated status checking here
            // For now, we'll assume everything is OK if service is connected
            val status = PrinterStatus(
                isConnected = true,
                paperStatus = PaperStatus.OK,
                temperature = TemperatureStatus.NORMAL
            )

            Log.d(TAG, "Printer status: $status")
            Result.success(status)

        } catch (e: Exception) {
            Log.e(TAG, "Failed to check printer status", e)
            Result.failure(e)
        }
    }

    override suspend fun initializePrinter(): Result<Unit> = withContext(Dispatchers.IO) {
        Log.d(TAG, "initializePrinter() called")

        try {
            if (!isServiceConnected || sunmiService == null) {
                Log.w(TAG, "Service not connected, attempting to connect...")
                initializeSunmiService()

                if (!waitForServiceConnection()) {
                    val error = "Printer service not available for initialization"
                    Log.e(TAG, error)
                    return@withContext Result.failure(Exception(error))
                }
            }

            val service = sunmiService ?: run {
                val error = "Printer service is null during initialization"
                Log.e(TAG, error)
                return@withContext Result.failure(Exception(error))
            }

            service.printerInit(null)
            Log.d(TAG, "Printer initialized successfully")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Printer initialization failed", e)
            Result.failure(e)
        }
    }

    private fun TextAlignment.toSunmiAlignment(): Int = when (this) {
        TextAlignment.LEFT -> 0
        TextAlignment.CENTER -> 1
        TextAlignment.RIGHT -> 2
    }.also { alignment ->
        Log.v(TAG, "TextAlignment $this -> $alignment")
    }

    private fun ImageAlignment.toSunmiAlignment(): Int = when (this) {
        ImageAlignment.LEFT -> 0
        ImageAlignment.CENTER -> 1
        ImageAlignment.RIGHT -> 2
    }.also { alignment ->
        Log.v(TAG, "ImageAlignment $this -> $alignment")
    }

    private fun BarcodeFormat.toZXingFormat(): com.google.zxing.BarcodeFormat = when (this) {
        BarcodeFormat.CODE128 -> com.google.zxing.BarcodeFormat.CODE_128
        BarcodeFormat.CODE39 -> com.google.zxing.BarcodeFormat.CODE_39
        BarcodeFormat.EAN13 -> com.google.zxing.BarcodeFormat.EAN_13
        BarcodeFormat.EAN8 -> com.google.zxing.BarcodeFormat.EAN_8
    }.also { format ->
        Log.v(TAG, "BarcodeFormat $this -> $format")
    }
}