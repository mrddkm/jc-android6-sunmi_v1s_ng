package com.arkhe.sunmi.presentation.viewmodel

import android.graphics.Bitmap
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

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _scanResults = MutableStateFlow<List<ScanResult>>(emptyList())
    val scanResults: StateFlow<List<ScanResult>> = _scanResults.asStateFlow()

    init {
        loadScanHistory()
    }

    // Print Functions
    fun printText(text: String) {
        if (text.isBlank()) {
            showError("Text cannot be empty")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            val printData = PrintData.Text(
                content = text,
                fontSize = 24,
                alignment = TextAlignment.LEFT
            )

            printUseCase.execute(printData)
                .onSuccess { showMessage("Text printed successfully") }
                .onFailure { showError("Failed to print: ${it.message}") }

            setLoading(false)
        }
    }

    fun printQRCode(content: String) {
        if (content.isBlank()) {
            showError("QR content cannot be empty")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            val printData = PrintData.QRCode(content = content, size = 200)

            printUseCase.execute(printData)
                .onSuccess { showMessage("QR Code printed successfully") }
                .onFailure { showError("Failed to print QR: ${it.message}") }

            setLoading(false)
        }
    }

    fun printBarcode(content: String) {
        if (content.isBlank()) {
            showError("Barcode content cannot be empty")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            val printData = PrintData.Barcode(
                content = content,
                format = BarcodeFormat.CODE128,
                width = 300,
                height = 100
            )

            printUseCase.execute(printData)
                .onSuccess { showMessage("Barcode printed successfully") }
                .onFailure { showError("Failed to print barcode: ${it.message}") }

            setLoading(false)
        }
    }

    fun printImage(bitmap: Bitmap) {
        viewModelScope.launch {
            setLoading(true)
            val printData = PrintData.Image(
                bitmap = bitmap,
                alignment = ImageAlignment.CENTER
            )

            printUseCase.execute(printData)
                .onSuccess { showMessage("Image printed successfully") }
                .onFailure { showError("Failed to print image: ${it.message}") }

            setLoading(false)
        }
    }

    fun printSampleReceipt() {
        viewModelScope.launch {
            setLoading(true)

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
                .onSuccess { showMessage("Receipt printed successfully") }
                .onFailure { showError("Failed to print receipt: ${it.message}") }

            setLoading(false)
        }
    }

    // Scanner Functions
    fun startScanning() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true)

            try {
                scanUseCase.startScan().collect { result ->
                    scanUseCase.saveScanResult(result)
                    loadScanHistory()
                    showMessage("Scanned: ${result.content}")
                }
            } catch (e: Exception) {
                showError("Scanner error: ${e.message}")
                _uiState.value = _uiState.value.copy(isScanning = false)
            }
        }
    }

    fun stopScanning() {
        viewModelScope.launch {
            scanUseCase.stopScan()
            _uiState.value = _uiState.value.copy(isScanning = false)
        }
    }

    fun clearScanHistory() {
        viewModelScope.launch {
            scanUseCase.clearHistory()
            loadScanHistory()
            showMessage("Scan history cleared")
        }
    }

    private fun loadScanHistory() {
        viewModelScope.launch {
            scanUseCase.getScanHistory().collect { results ->
                _scanResults.value = results
            }
        }
    }

    // UI State Management
    private fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    private fun showMessage(message: String) {
        _uiState.value = _uiState.value.copy(message = message, error = null)
    }

    private fun showError(error: String) {
        _uiState.value = _uiState.value.copy(error = error, message = null)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }
}