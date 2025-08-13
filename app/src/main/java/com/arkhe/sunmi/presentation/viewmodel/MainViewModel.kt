package com.arkhe.sunmi.presentation.viewmodel

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkhe.sunmi.domain.model.PrintData
import com.arkhe.sunmi.domain.model.ScanResult
import com.arkhe.sunmi.domain.usecase.PrintUseCase
import com.arkhe.sunmi.domain.usecase.ScanUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val printUseCase: PrintUseCase,
    private val scanUseCase: ScanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _scanResults = MutableStateFlow<List<ScanResult>>(emptyList())
    val scanResults: StateFlow<List<ScanResult>> = _scanResults.asStateFlow()

    fun printText(text: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            printUseCase.execute(PrintData.Text(text)).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "Text printed successfully"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Print failed: ${error.message}"
                    )
                }
            )
        }
    }

    fun printQRCode(content: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            printUseCase.execute(PrintData.QRCode(content)).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "QR Code printed successfully"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Print failed: ${error.message}"
                    )
                }
            )
        }
    }

    fun printBarcode(content: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            printUseCase.execute(PrintData.Barcode(content)).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "Barcode printed successfully"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Print failed: ${error.message}"
                    )
                }
            )
        }
    }

    fun printImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            printUseCase.execute(PrintData.Image(bitmap)).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "Image printed successfully"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Print failed: ${error.message}"
                    )
                }
            )
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun printSampleReceipt() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val receiptItems = listOf(
                PrintData.Text("SUNMI V1S DEMO", fontSize = 28, isBold = true),
                PrintData.LineFeed,
                PrintData.Text(
                    "Date: ${
                        java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date())
                    }"
                ),
                PrintData.LineFeed,
                PrintData.Text("Items:"),
                PrintData.Text("- Coffee        $3.50"),
                PrintData.Text("- Sandwich      $5.25"),
                PrintData.LineFeed,
                PrintData.Text("Total: $8.75", isBold = true),
                PrintData.LineFeed,
                PrintData.QRCode("https://sunmi.com"),
                PrintData.LineFeed,
                PrintData.CutPaper
            )

            printUseCase.printReceipt(receiptItems).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "Receipt printed successfully"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Print failed: ${error.message}"
                    )
                }
            )
        }
    }

    fun startScanning() {
        viewModelScope.launch {
            scanUseCase.startScan().collect { result ->
                val currentResults = _scanResults.value.toMutableList()
                currentResults.add(0, result) // Add to top
                _scanResults.value = currentResults.take(10) // Keep last 10 results

                _uiState.value = _uiState.value.copy(
                    message = "Scanned: ${result.content}"
                )
            }
        }
    }

    fun stopScanning() {
        viewModelScope.launch {
            scanUseCase.stopScan()
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }
}

data class MainUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)