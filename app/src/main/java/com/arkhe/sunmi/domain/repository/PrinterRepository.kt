package com.arkhe.sunmi.domain.repository

import com.arkhe.sunmi.domain.model.PrintData
import com.arkhe.sunmi.domain.model.PrinterStatus

interface PrinterRepository {
    suspend fun print(data: PrintData): Result<Unit>
    suspend fun printReceipt(items: List<PrintData>): Result<Unit>
    suspend fun checkPrinterStatus(): Result<PrinterStatus>
    suspend fun initializePrinter(): Result<Unit>
}