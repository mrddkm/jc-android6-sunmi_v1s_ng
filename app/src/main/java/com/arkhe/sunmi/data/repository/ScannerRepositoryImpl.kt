package com.arkhe.sunmi.data.repository

import com.arkhe.sunmi.data.local.ScanHistoryDao
import com.arkhe.sunmi.data.local.ScanHistoryEntity
import com.arkhe.sunmi.domain.model.ScanResult
import com.arkhe.sunmi.domain.repository.ScannerRepository
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.CompoundBarcodeView
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class ScannerRepositoryImpl(
    private val scanHistoryDao: ScanHistoryDao
) : ScannerRepository {

    private var barcodeView: CompoundBarcodeView? = null

    fun setBarcodeView(view: CompoundBarcodeView) {
        this.barcodeView = view
    }

    override suspend fun startScan(): Flow<ScanResult> = callbackFlow {
        val callback = BarcodeCallback { result ->
            val scanResult = ScanResult(
                content = result.text,
                format = result.barcodeFormat.name
            )
            trySend(scanResult)
        }

        barcodeView?.decodeContinuous(callback)

        awaitClose {
            barcodeView?.pause()
        }
    }

    override suspend fun stopScan() {
        barcodeView?.pause()
    }

    override suspend fun getScanHistory(): Flow<List<ScanResult>> {
        return scanHistoryDao.getAllScans().map { entities ->
            entities.map { entity ->
                ScanResult(
                    id = entity.id,
                    content = entity.content,
                    format = entity.format,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    override suspend fun saveScanResult(result: ScanResult) {
        val entity = ScanHistoryEntity(
            content = result.content,
            format = result.format,
            timestamp = result.timestamp
        )
        scanHistoryDao.insertScan(entity)
    }

    override suspend fun clearHistory() {
        scanHistoryDao.deleteAllScans()
    }
}