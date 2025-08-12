package com.arkhe.sunmi.domain.repository

import com.arkhe.sunmi.domain.model.ScanResult
import kotlinx.coroutines.flow.Flow

interface ScannerRepository {
    suspend fun startScan(): Flow<ScanResult>
    suspend fun stopScan()
    suspend fun getScanHistory(): Flow<List<ScanResult>>
    suspend fun saveScanResult(result: ScanResult)
    suspend fun clearHistory()
}