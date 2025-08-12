package com.arkhe.sunmi.domain.usecase

import com.arkhe.sunmi.domain.model.ScanResult
import com.arkhe.sunmi.domain.repository.ScannerRepository
import kotlinx.coroutines.flow.Flow

class ScanUseCase(
    private val scannerRepository: ScannerRepository
) {
    suspend fun startScan(): Flow<ScanResult> {
        return scannerRepository.startScan()
    }

    suspend fun stopScan() {
        scannerRepository.stopScan()
    }

    suspend fun getScanHistory(): Flow<List<ScanResult>> {
        return scannerRepository.getScanHistory()
    }

    suspend fun saveScanResult(result: ScanResult) {
        scannerRepository.saveScanResult(result)
    }

    suspend fun clearHistory() {
        scannerRepository.clearHistory()
    }
}