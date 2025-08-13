package com.arkhe.sunmi.presentation.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkhe.sunmi.domain.model.BarcodeFormat
import com.arkhe.sunmi.domain.model.ImageAlignment
import com.arkhe.sunmi.domain.model.PrintData
import com.arkhe.sunmi.domain.model.ScanResult
import com.arkhe.sunmi.domain.model.TextAlignment
import com.arkhe.sunmi.domain.usecase.PrintUseCase
import com.arkhe.sunmi.domain.usecase.ScanUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class UiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val isScanning: Boolean = false
)

class MainViewModel(
    private val printUseCase: PrintUseCase,
    private val scanUseCase: ScanUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _scanResults = MutableStateFlow<List<ScanResult>>(emptyList())
    val scanResults: StateFlow<List<ScanResult>> = _scanResults.asStateFlow()

    init {
        Log.d(TAG, "MainViewModel initialized")
        loadScanHistory()
    }

    // Print Functions
    fun printText(text: String) {
        Log.d(TAG, "printText called with: $text")

        if (text.isBlank()) {
            Log.w(TAG, "Text is blank")
            showError("Text cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                setLoading(true)
                Log.d(TAG, "Starting text print operation")

                val printData = PrintData.Text(
                    content = text,
                    fontSize = 24,
                    alignment = TextAlignment.LEFT
                )

                printUseCase.execute(printData)
                    .onSuccess {
                        Log.d(TAG, "Text printed successfully")
                        showMessage("Text printed successfully")
                    }
                    .onFailure {
                        Log.e(TAG, "Failed to print text", it)
                        showError("Failed to print: ${it.message}")
                    }

            } catch (e: Exception) {
                Log.e(TAG, "Exception in printText", e)
                showError("Unexpected error: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    fun printQRCode(content: String) {
        Log.d(TAG, "printQRCode called with: $content")

        if (content.isBlank()) {
            Log.w(TAG, "QR content is blank")
            showError("QR content cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                setLoading(true)
                Log.d(TAG, "Starting QR code print operation")

                val printData = PrintData.QRCode(content = content, size = 200)

                printUseCase.execute(printData)
                    .onSuccess {
                        Log.d(TAG, "QR Code printed successfully")
                        showMessage("QR Code printed successfully")
                    }
                    .onFailure {
                        Log.e(TAG, "Failed to print QR code", it)
                        showError("Failed to print QR: ${it.message}")
                    }

            } catch (e: Exception) {
                Log.e(TAG, "Exception in printQRCode", e)
                showError("Unexpected error: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    fun printBarcode(content: String) {
        Log.d(TAG, "printBarcode called with: $content")

        if (content.isBlank()) {
            Log.w(TAG, "Barcode content is blank")
            showError("Barcode content cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                setLoading(true)
                Log.d(TAG, "Starting barcode print operation")

                val printData = PrintData.Barcode(
                    content = content,
                    format = BarcodeFormat.CODE128,
                    width = 300,
                    height = 100
                )

                printUseCase.execute(printData)
                    .onSuccess {
                        Log.d(TAG, "Barcode printed successfully")
                        showMessage("Barcode printed successfully")
                    }
                    .onFailure {
                        Log.e(TAG, "Failed to print barcode", it)
                        showError("Failed to print barcode: ${it.message}")
                    }

            } catch (e: Exception) {
                Log.e(TAG, "Exception in printBarcode", e)
                showError("Unexpected error: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    fun printImage(bitmap: Bitmap) {
        Log.d(TAG, "printImage called")

        viewModelScope.launch {
            try {
                setLoading(true)
                Log.d(TAG, "Starting image print operation")

                val printData = PrintData.Image(
                    bitmap = bitmap,
                    alignment = ImageAlignment.CENTER
                )

                printUseCase.execute(printData)
                    .onSuccess {
                        Log.d(TAG, "Image printed successfully")
                        showMessage("Image printed successfully")
                    }
                    .onFailure {
                        Log.e(TAG, "Failed to print image", it)
                        showError("Failed to print image: ${it.message}")
                    }

            } catch (e: Exception) {
                Log.e(TAG, "Exception in printImage", e)
                showError("Unexpected error: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    fun printSampleReceipt() {
        Log.d(TAG, "printSampleReceipt called")

        viewModelScope.launch {
            try {
                setLoading(true)
                Log.d(TAG, "Starting sample receipt print operation")

                val receiptItems = listOf(
                    PrintData.Text(
                        content = "ARKHE STORE",
                        fontSize = 28,
                        isBold = true,
                        alignment = TextAlignment.CENTER
                    ),
                    PrintData.LineFeed,
                    PrintData.Text("================================"),
                    PrintData.Text("Item 1                    10.00"),
                    PrintData.Text("Item 2                    15.50"),
                    PrintData.Text("Item 3                     8.75"),
                    PrintData.Text("================================"),
                    PrintData.Text(
                        content = "TOTAL:                    34.25",
                        isBold = true
                    ),
                    PrintData.LineFeed,
                    PrintData.QRCode("https://arkhe.com/receipt/12345"),
                    PrintData.LineFeed,
                    PrintData.CutPaper
                )

                printUseCase.printReceipt(receiptItems)
                    .onSuccess {
                        Log.d(TAG, "Sample receipt printed successfully")
                        showMessage("Receipt printed successfully")
                    }
                    .onFailure {
                        Log.e(TAG, "Failed to print receipt", it)
                        showError("Failed to print receipt: ${it.message}")
                    }

            } catch (e: Exception) {
                Log.e(TAG, "Exception in printSampleReceipt", e)
                showError("Unexpected error: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    // Scanner Functions
    fun startScanning() {
        Log.d(TAG, "startScanning called")

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isScanning = true)
                Log.d(TAG, "Starting scan operation")

                scanUseCase.startScan()
                    .catch { e ->
                        Log.e(TAG, "Error in scan flow", e)
                        showError("Scanner error: ${e.message}")
                        _uiState.value = _uiState.value.copy(isScanning = false)
                    }
                    .collect { result ->
                        Log.d(TAG, "Scan result received: ${result.content}")
                        scanUseCase.saveScanResult(result)
                        loadScanHistory()
                        showMessage("Scanned: ${result.content}")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in startScanning", e)
                showError("Scanner error: ${e.message}")
                _uiState.value = _uiState.value.copy(isScanning = false)
            }
        }
    }

    fun stopScanning() {
        Log.d(TAG, "stopScanning called")

        viewModelScope.launch {
            try {
                scanUseCase.stopScan()
                _uiState.value = _uiState.value.copy(isScanning = false)
                Log.d(TAG, "Scanning stopped successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Exception in stopScanning", e)
                showError("Error stopping scanner: ${e.message}")
            }
        }
    }

    fun clearScanHistory() {
        Log.d(TAG, "clearScanHistory called")

        viewModelScope.launch {
            try {
                scanUseCase.clearHistory()
                loadScanHistory()
                showMessage("Scan history cleared")
                Log.d(TAG, "Scan history cleared successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Exception in clearScanHistory", e)
                showError("Error clearing history: ${e.message}")
            }
        }
    }

    private fun loadScanHistory() {
        Log.d(TAG, "loadScanHistory called")

        viewModelScope.launch {
            try {
                scanUseCase.getScanHistory()
                    .catch { e ->
                        Log.e(TAG, "Error loading scan history", e)
                        showError("Error loading scan history: ${e.message}")
                    }
                    .collect { results ->
                        Log.d(TAG, "Scan history loaded: ${results.size} items")
                        _scanResults.value = results
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in loadScanHistory", e)
                showError("Error loading scan history: ${e.message}")
            }
        }
    }

    // UI State Management
    private fun setLoading(isLoading: Boolean) {
        Log.d(TAG, "setLoading: $isLoading")
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    private fun showMessage(message: String) {
        Log.d(TAG, "showMessage: $message")
        _uiState.value = _uiState.value.copy(message = message, error = null)
    }

    private fun showError(error: String) {
        Log.e(TAG, "showError: $error")
        _uiState.value = _uiState.value.copy(error = error, message = null)
    }

    fun clearMessage() {
        Log.d(TAG, "clearMessage called")
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "MainViewModel onCleared")
    }
}