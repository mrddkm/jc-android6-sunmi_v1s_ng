package com.arkhe.sunmi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkhe.sunmi.domain.model.ScanResult
import com.arkhe.sunmi.domain.usecase.ScanUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScanViewModel(
    private val scanUseCase: ScanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private val _scanResults = MutableStateFlow<List<ScanResult>>(emptyList())
    val scanResults: StateFlow<List<ScanResult>> = _scanResults.asStateFlow()

    init {
        loadScanHistory()
    }

    fun startScanning() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true)

            scanUseCase.startScan().collect { result ->
                // Save to database
                scanUseCase.saveScanResult(result)

                _uiState.value = _uiState.value.copy(
                    lastScanResult = result,
                    message = "Scanned: ${result.content}"
                )

                // Update scan results list
                loadScanHistory()
            }
        }
    }

    fun stopScanning() {
        viewModelScope.launch {
            scanUseCase.stopScan()
            _uiState.value = _uiState.value.copy(isScanning = false)
        }
    }

    private fun loadScanHistory() {
        viewModelScope.launch {
            scanUseCase.getScanHistory().collect { results ->
                _scanResults.value = results
            }
        }
    }

    fun clearScanHistory() {
        viewModelScope.launch {
            scanUseCase.clearHistory()
            _uiState.value = _uiState.value.copy(message = "History cleared")
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }
}

data class ScanUiState(
    val isScanning: Boolean = false,
    val lastScanResult: ScanResult? = null,
    val message: String? = null,
    val error: String? = null
)