package com.arkhe.sunmi.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkhe.sunmi.domain.model.PrintData
import com.arkhe.sunmi.domain.model.PrinterStatus
import com.arkhe.sunmi.domain.usecase.PrintUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrintViewModel(
    private val printUseCase: PrintUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrintUiState())
    val uiState: StateFlow<PrintUiState> = _uiState.asStateFlow()

    init {
        initializePrinter()
        checkPrinterStatus()
    }

    fun printText(text: String, fontSize: Int = 24, isBold: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            printUseCase.execute(
                PrintData.Text(
                    content = text,
                    fontSize = fontSize,
                    isBold = isBold
                )
            ).fold(
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

    fun printQRCode(content: String, size: Int = 200) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            printUseCase.execute(PrintData.QRCode(content, size)).fold(
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

    private fun initializePrinter() {
        viewModelScope.launch {
            printUseCase.initializePrinter()
        }
    }

    private fun checkPrinterStatus() {
        viewModelScope.launch {
            printUseCase.checkPrinterStatus().fold(
                onSuccess = { status ->
                    _uiState.value = _uiState.value.copy(printerStatus = status)
                },
                onFailure = {
                    // Handle error silently or show message
                }
            )
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }
}

data class PrintUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val printerStatus: PrinterStatus? = null
)